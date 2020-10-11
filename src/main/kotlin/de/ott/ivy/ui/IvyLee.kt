package de.ott.ivy.ui

import de.ott.ivy.config.Configuration
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.gdrive.ConnectionProvider
import de.ott.ivy.gdrive.RemoteFilesHandler
import de.ott.ivy.ui.event.IvyLeeEventHandler
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.web.WebView
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.View
import tornadofx.minus
import java.io.File
import java.lang.Integer.max
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set
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

    val eventHandler = IvyLeeEventHandler(anchorPane, taskList, toolBar, addButton)

    init {
        // set scroll speed
        val SPEED = 0.00175
        root.content.onScroll = EventHandler {
            root.vvalue -= it.deltaY * SPEED
        }
        addButtonPane.prefWidthProperty().bind( root.widthProperty() )
        addButton.xProperty().bind(addButtonPane.widthProperty().minus( addButton.fitWidthProperty() ).minus(15))

        Thread.currentThread().name = MAIN_THREAD_NAME
        var oldTasks: List<IvyLeeTask>? = null
        try {
            val tasksFile = File("tasks.db")
            println("downloading tasks from gdrive")
            gdrive.readTasks(tasksFile)
            println("loading tasks from file ${tasksFile.absolutePath ?: "NONE"}")
            oldTasks = Cbor.decodeFromByteArray(ListSerializer(IvyLeeTask.serializer()), tasksFile.readBytes())
            oldTasks.forEach(::println)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (t: Throwable) {
            println("No data stored..")
        }

        val lSize = oldTasks?.size?:1
        anchorPane.prefHeight = min(200 * 4, max(lSize * 200, 200)).toDouble()
        root.isFitToHeight = true
        root.isFitToWidth = true

        taskList.children.removeAll { it is BorderPane }
        val tmp = AtomicBoolean(false)
        oldTasks?.ifEmpty { listOf(IvyLeeTask(), IvyLeeTask(), IvyLeeTask()) }?.forEach { task ->
            println("Create borderPane for task: $task")
            // create contaienr
            val bp = FXMLLoader().apply {
                location = classLoader.getResource("views/TaskCell.fxml")
            }.load<BorderPane>()

            bp.minWidthProperty().bind(root.widthProperty().minus(15))
            bp.minHeightProperty().bind(root.heightProperty().minus(toolBar.heightProperty())
                    .divide(min(4, max(1, oldTasks?.size?:1)))          //  if less than 4 tasks, scale - scale for 4 tasks max, then scroll for other tasks
                    .minus(1.35))                                          //  border with is 2 px

            bp.onMouseEntered = EventHandler { eventHandler.mark(it) }
            bp.onMouseExited = EventHandler { eventHandler.unmark(it) }
            bp.onMousePressed = EventHandler { eventHandler.onClick(it) }

            var title: Label? = null
            var desc: WebView? = null
            var time: Label? = null
            var status: ProgressIndicator? = null
            var progress: ProgressBar? = null
            var progressAdditional: ProgressBar? = null
            bp.children.forEach { bpChild ->
                println("curr child: $bpChild")
                when (bpChild) {
                    is Label -> title = bpChild
                    is WebView -> desc = bpChild
                    is HBox -> {
                        bpChild.children.forEach { hbChild ->
                            when (hbChild) {
                                is ProgressBar -> {
                                    if (hbChild.id.matches(Regex(""".*additional.*""")))
                                        progressAdditional = hbChild
                                    else progress = hbChild
                                }
                                is Label -> time = hbChild
                                is ProgressIndicator -> status = hbChild
                            }
                        }
                    }
                }
            }
            taskList.children.add(bp)
            tasks[TaskCellContainer(bp, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!)] = task

            tasks.forEach(::println)
            tasks.forEach(eventHandler::updateCell)
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
                    tasks.writeBytes(Cbor.encodeToByteArray(ListSerializer(IvyLeeTask.serializer()), IvyLee.tasks.values.toList()))
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
