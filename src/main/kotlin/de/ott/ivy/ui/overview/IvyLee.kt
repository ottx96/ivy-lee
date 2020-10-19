package de.ott.ivy.ui.overview

import de.ott.ivy.config.Configuration
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.gdrive.ConnectionProvider
import de.ott.ivy.gdrive.RemoteFilesHandler
import de.ott.ivy.ui.overview.event.IvyLeeEventHandler
import de.ott.ivy.ui.overview.impl.ComponentBuilder
import de.ott.ivy.ui.overview.impl.TaskCellUpdater
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
import java.lang.Thread.sleep
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

    val eventHandler = IvyLeeEventHandler(taskList)

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
                    .divide(min(4, max(1, oldTasks?.size?:1)))          //  if less than 4 tasks, scale - scale for 4 tasks max, then scroll for other tasks
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
        Thread {
            while (true) {
                var ct = 0
                try {
                    sleep(1000 * 60 * 15) // 15 Minuten
                    if (++ct % 10 == 0) // 150 Minuten
                        gdrive.cleanupFilesOlderThan(Configuration.instance.cleanInterval, Configuration.instance.timeUnit)
                    // write file
                    println("syncing tasks to gdrive..")
                    val tasks = File("tasks-auto-sync.db")
                    tasks.writeBytes(Cbor.encodeToByteArray(ListSerializer(IvyLeeTask.serializer()), Companion.tasks.values.toList()))
                    gdrive.saveTasks(tasks)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

}
