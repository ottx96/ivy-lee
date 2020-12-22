package com.github.ottx96.ivy.ui.overview

import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.data.TaskCellContainer
import com.github.ottx96.ivy.gdrive.ConnectionProvider
import com.github.ottx96.ivy.gdrive.RemoteFilesHandler
import com.github.ottx96.ivy.ui.overview.event.IvyLeeEventHandler
import com.github.ottx96.ivy.ui.overview.impl.ComponentBuilder
import com.github.ottx96.ivy.ui.overview.impl.TaskCellUpdater
import com.github.ottx96.ivy.ui.overview.threading.SyncRunnable
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.View
import tornadofx.minus
import java.io.File
import kotlin.math.max
import kotlin.math.min


/**
 * Main program (UI)
 * Project: ivy-lee
 * Package:
 * Created: 28.01.2020 15:55
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
@ExperimentalSerializationApi
class IvyLee : View("Ivy-Lee Tracking") {
    override val root: ScrollPane by fxml("/views/IvyLee.fxml")

    companion object {
        const val MAIN_THREAD_NAME = "UI_THREAD"
        val tasks = mapOf<TaskCellContainer, IvyLeeTask>().toMutableMap()

        val gdrive = RemoteFilesHandler(ConnectionProvider.connect())

        fun Map<TaskCellContainer, IvyLeeTask>.getCellByBorderPane(bp: BorderPane?) = keys.first { it.borderPane == bp!! }
        fun Map<TaskCellContainer, IvyLeeTask>.getTaskByBorderPane(bp: BorderPane?) = tasks[keys.first { it.borderPane == bp!! }]
    }

    val anchorPane: AnchorPane by fxid("anchor_pane")
    val taskList: VBox by fxid("tasklist")
    val toolBar: HBox by fxid("tool_bar")
    val addButton: ImageView by fxid("add")
    val addButtonPane: Pane by fxid("pane_add")
    val settingsButton: ImageView by fxid("settings")

    private val eventHandler = IvyLeeEventHandler(taskList)

    init {
        // set scroll speed
        val SPEED = 0.00175
        root.content.onScroll = EventHandler {
            root.vvalue -= it.deltaY * SPEED
        }

        addButtonPane.prefWidthProperty().bind( root.widthProperty() )
        addButton.xProperty().bind(addButtonPane.widthProperty().minus( addButton.fitWidthProperty() ).minus(15))

        addButton.setOnMouseClicked {
            val task = IvyLeeTask()
            val taskBuilt = ComponentBuilder.createTaskContainer(task)
            taskList.children.add(taskBuilt.second)
            tasks[taskBuilt.first] = task

            tasks.forEach { it.key.borderPane.apply {
                minHeightProperty().unbind()
                minWidthProperty().unbind()
                minWidthProperty().bind(root.widthProperty().minus(15))
                minHeightProperty().bind(root.heightProperty().minus(toolBar.heightProperty())
                        .divide(min(4, max(1, tasks.size)))          //  if less than 4 tasks, scale - scale for 4 tasks max, then scroll for other tasks
                        .minus(1.35))                                //  border width is ~2 px

                onMouseEntered = EventHandler { eventHandler.mark(it) }
                onMouseExited = EventHandler { eventHandler.unmark(it) }
                onMousePressed = EventHandler { eventHandler.onClick(it) }
            }}

            TaskCellUpdater.updateTaskCell(task, taskBuilt.first)
        }

        Thread.currentThread().name = MAIN_THREAD_NAME
        var oldTasks: List<IvyLeeTask>? = null
        try {
            val tasksFile = File("tasks.db")
            println("downloading tasks from gdrive")
            gdrive.readTasks(tasksFile)
            println("loading tasks from file ${tasksFile.absolutePath ?: "NONE"}")
            oldTasks = Cbor{ ignoreUnknownKeys = true }.decodeFromByteArray(ListSerializer(IvyLeeTask.serializer()), tasksFile.readBytes())
            oldTasks.forEach(::println)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (t: Throwable) {
            println("No data stored..")
        }

        taskList.children.removeAll { it is BorderPane }
        oldTasks?.forEach { task ->
            val built = ComponentBuilder.createTaskContainer(task)
            val bp = built.second

            taskList.children.add(bp)
            tasks[built.first] = task

            bp.minWidthProperty().bind(root.widthProperty().minus(15))
            bp.minHeightProperty().bind(root.heightProperty().minus(toolBar.heightProperty())
                    .divide(min(4, max(1, oldTasks.size)))          //  if less than 4 tasks, scale - scale for 4 tasks max, then scroll for other tasks
                    .minus(1.35))                                          //  border with is 2 px

            bp.onMouseEntered = EventHandler { eventHandler.mark(it) }
            bp.onMouseExited = EventHandler { eventHandler.unmark(it) }
            bp.onMousePressed = EventHandler { eventHandler.onClick(it) }

            tasks.forEach(::println)
            TaskCellUpdater.updateTaskCell(task, built.first)
        }
        when {
            tasks.isEmpty() -> root.prefHeight = 200.0
            tasks.size > 4  -> root.prefHeight = 800.0
            else            -> root.prefHeight = tasks.size * 200.0
        }
    }

    init {
        // auto-sync tasks to gdrive
        Thread(SyncRunnable(gdrive)).apply {
            isDaemon = true
            start()
        }
    }

}
