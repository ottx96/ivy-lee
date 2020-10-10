package de.ott.ivy.ui

import de.ott.ivy.config.Configuration
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.data.enum.TaskStatus
import de.ott.ivy.gdrive.ConnectionProvider
import de.ott.ivy.gdrive.RemoteFilesHandler
import de.ott.ivy.html.MarkdownParser
import de.ott.ivy.ui.dialog.TaskDialog
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.CssBox
import tornadofx.View
import tornadofx.style
import java.io.File
import java.lang.Thread.sleep
import kotlin.collections.set

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
    override val root: AnchorPane by fxml("/views/IvyLee2.fxml")

    companion object {
        const val MAIN_THREAD_NAME = "UI_THREAD"

        val tasks = mapOf<TaskCellContainer, IvyLeeTask>().toMutableMap()
        val gdrive = RemoteFilesHandler(ConnectionProvider.connect())

        fun Map<TaskCellContainer, IvyLeeTask>.getCellByBorderPane(bp: BorderPane?) = keys.first { it.borderPane == bp!! }
        fun Map<TaskCellContainer, IvyLeeTask>.getTaskByBorderPane(bp: BorderPane?) = tasks[keys.first { it.borderPane == bp!! }]
    }

    val taskList: VBox by fxid("tasklist")

    init {
        Thread.currentThread().name = MAIN_THREAD_NAME
        var oldTasks: List<IvyLeeTask?>? = null
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

        taskList.children.clear()
        oldTasks!!.ifEmpty { listOf(IvyLeeTask(), IvyLeeTask(), IvyLeeTask()) }.forEach { task ->
            println("Create borderPane for task: $task")
            // create contaienr
            val bp = FXMLLoader().apply {
                location = classLoader.getResource("views/TaskCell.fxml")
            }.load<BorderPane>()

            bp.onMouseEntered = EventHandler { mark(it) }
            bp.onMouseExited = EventHandler { unmark(it) }
            bp.onMousePressed = EventHandler { onClick(it) }

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
            tasks[TaskCellContainer(bp, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!)] = task?: IvyLeeTask()

            tasks.forEach(::println)
            tasks.forEach(::updateCell)

//        listOf(bpTopLeft, bpTopRight, bpMiddleLeft, bpMiddleRight, bpBottomLeft, bpBottomRight)
//            .zip(oldTasks?:listOf(null, null, null, null, null, null)
//            .sortedBy { (Math.random() * 100).toInt() }).forEach { bp ->
//            var title: Label? = null
//            var desc: WebView? = null
//            var time: Label? = null
//            var status: ProgressIndicator? = null
//            var progress: ProgressBar? = null
//            var progressAdditional: ProgressBar? = null
//            bp.first.children.forEach {bpChild ->
//                when(bpChild){
//                    is Label -> title = bpChild
//                    is WebView -> desc = bpChild
//                    is HBox -> {
//                        bpChild.children.forEach { hbChild ->
//                            when(hbChild){
//                                is ProgressBar -> {
//                                    if(hbChild.id.matches(Regex(""".*additional.*""")))
//                                        progressAdditional = hbChild
//                                    else progress = hbChild
//                                }
//                                is Label -> time = hbChild
//                                is ProgressIndicator -> status = hbChild
//                            }
//                        }
//                    }
//                }
//            }
//            println(desc)
//            // intanciate class and put it in the map
//            tasks[TaskGridCellContainer(bp.first, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!)] = bp.second ?: IvyLeeTask()
//        }
//        tasks.forEach(::println)
//        tasks.forEach(::updateCell)
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

    fun mark(event: MouseEvent) {
        (event.target as BorderPane).style {
            borderColor += CssBox(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            backgroundColor += tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color.desaturate()
        }
    }

    fun unmark(event: MouseEvent) {
        (event.target as BorderPane).style {
            backgroundColor += tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }

    fun onClick(event: MouseEvent) {
        if (event.isPrimaryButtonDown)
            (event.target as BorderPane).apply {
                val task = tasks.getTaskByBorderPane(this)!!
                when (task.status) {
                    TaskStatus.EMPTY -> {
                        return
                    }
                    TaskStatus.UNDONE -> task.status = TaskStatus.IN_WORK
                    TaskStatus.IN_WORK -> task.status = TaskStatus.DONE
                    TaskStatus.DONE -> task.status = TaskStatus.UNDONE
                }
                updateCell(tasks.getCellByBorderPane(this), task)
                if (task.status == TaskStatus.IN_WORK) {
                    // stop other timers
                    tasks.values.filter { it.status == TaskStatus.IN_WORK && it != task }.forEach {
                        it.status = TaskStatus.UNDONE
                    }
                    Thread {
                        val threadCell = tasks.getCellByBorderPane(this)
                        while (task.status == TaskStatus.IN_WORK) {
                            task.timeInvestedSeconds++
                            updateCell(threadCell, task)
                            sleep(1000) // sleep 1s
                        }
                    }.start()
                }
            }
        else {
            // open custom dialog
            (event.target as BorderPane).apply {
                tasks[tasks.getCellByBorderPane(this)] = TaskDialog.showDialog(tasks.getTaskByBorderPane(this)!!)
                updateCell(tasks.getCellByBorderPane(this), tasks.getTaskByBorderPane(this)!!)
            }
            mark(event)
        }
    }

    private fun updateCell(cellContainer: TaskCellContainer, task: IvyLeeTask) {
        println("updating cell with task: $task")

        cellContainer.titleLabel.text = task.name
        if (cellContainer.descLabel.engine.userStyleSheetLocation.isNullOrBlank())
            cellContainer.descLabel.engine.userStyleSheetLocation = javaClass.getResource("/de/ott/ivy/css/style.css").toString()
        cellContainer.timeLabel.text = "${task.estTimeSeconds / 60.0} m"

        cellContainer.progressBar.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        cellContainer.statusIndicator.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        if (task.status == TaskStatus.DONE) cellContainer.statusIndicator.progress = 1.0

        if (task.timeInvestedSeconds >= task.estTimeSeconds)
            cellContainer.progressBarAdditional.progress = (task.timeInvestedSeconds - task.estTimeSeconds) * 1.0 / task.estTimeSeconds
        else
            cellContainer.progressBarAdditional.progress = 0.0

        cellContainer.borderPane.style {
            backgroundColor += task.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }

        if (Thread.currentThread().name == MAIN_THREAD_NAME)
            cellContainer.descLabel.engine.loadContent(MarkdownParser.convertHtml(task.descr), "text/html")
    }
}
