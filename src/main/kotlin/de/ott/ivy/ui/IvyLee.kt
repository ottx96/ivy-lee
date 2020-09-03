package de.ott.ivy.ui

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.ui.dialog.TaskDialog
import de.ott.ivy.data.TaskGridCellContainer
import de.ott.ivy.data.enum.TaskStatus
import de.ott.ivy.gdrive.ApplicationDataHandler
import de.ott.ivy.gdrive.ConnectionProvider
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.CssBox
import tornadofx.View
import tornadofx.style
import java.io.File
import java.lang.Thread.sleep
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.listOf
import kotlin.collections.mapOf
import kotlin.collections.set
import kotlin.collections.sortedBy
import kotlin.collections.toSortedMap
import kotlin.collections.zip

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
    override val root: GridPane by fxml("/views/IvyLee.fxml")

    companion object{
        val tasks = mapOf<TaskGridCellContainer, IvyLeeTask>().toSortedMap()
        val gdrive = ApplicationDataHandler(ConnectionProvider.connect())

        fun Map<TaskGridCellContainer, IvyLeeTask>.getCellByBorderPane(bp: BorderPane?) = keys.first { it.borderPane == bp!! }
        fun Map<TaskGridCellContainer, IvyLeeTask>.getTaskByBorderPane(bp: BorderPane?) = tasks[keys.first { it.borderPane == bp!! }]
    }

    val bpTopLeft: BorderPane by fxid("bp_topleft")
    val bpTopRight: BorderPane by fxid("bp_topright")
    val bpMiddleLeft: BorderPane by fxid("bp_middleleft")
    val bpMiddleRight: BorderPane by fxid("bp_middleright")
    val bpBottomLeft: BorderPane by fxid("bp_bottomleft")
    val bpBottomRight: BorderPane by fxid("bp_bottomright")

    init{
        var oldTasks: List<IvyLeeTask?>? = null
        try{
            val tasksFile = File("tasks.db")
            println("downloading tasks from gdrive")
            gdrive.readTasks(tasksFile)
            println("loading tasks from file ${tasksFile.absolutePath?:"NONE"}")
            oldTasks = Cbor.decodeFromByteArray(ListSerializer(IvyLeeTask.serializer()), tasksFile.readBytes())
            oldTasks.forEach ( ::println )
        }catch(e: Exception){
            e.printStackTrace()
        }catch (t: Throwable){
            println("No data stored..")
        }

        listOf(bpTopLeft, bpTopRight, bpMiddleLeft, bpMiddleRight, bpBottomLeft, bpBottomRight).zip(oldTasks?:listOf(null, null, null, null, null, null).sortedBy { (Math.random() * 100).toInt() }).forEach { bp ->
            var title: Label? = null
            var desc: TextArea? = null
            var time: Label? = null
            var status: ProgressIndicator? = null
            var progress: ProgressBar? = null
            var progressAdditional: ProgressBar? = null
            bp.first.children.forEach {bpChild ->
                when(bpChild){
                    is Label -> title = bpChild
                    is TextArea -> desc = bpChild
                    is HBox -> {
                        bpChild.children.forEach { hbChild ->
                            when(hbChild){
                                is ProgressBar -> {
                                    if(hbChild.id.matches(Regex(""".*additional.*""")))
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
            // Klasse instanziieren und in Map einordnen
            tasks[TaskGridCellContainer(bp.first, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!)] = bp.second ?: IvyLeeTask()
        }
        tasks.forEach(::updateCell)
        tasks.forEach(::println)
    }
    
    fun mark(event: MouseEvent){
        (event.target as BorderPane).style {
            borderColor += CssBox(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            //backgroundColor += tasks[event.target as BorderPane]!!.status.color.desaturate()
            backgroundColor += tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color.desaturate()
        }
    }

    fun unmark(event: MouseEvent){
        (event.target as BorderPane).style {
            backgroundColor += tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }

    fun onClick(event: MouseEvent){
        if(event.isPrimaryButtonDown)
            (event.target as BorderPane).apply {
                val task = tasks.getTaskByBorderPane(this)!!
                when(task.status){
                    TaskStatus.EMPTY -> { return }
                    TaskStatus.UNDONE -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.IN_WORK
                    TaskStatus.IN_WORK -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.DONE
                    TaskStatus.DONE -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.UNDONE
                }
                updateCell(tasks.getCellByBorderPane(this), task)
                if(task.status == TaskStatus.IN_WORK){
                    Thread{
                        val threadCell = tasks.getCellByBorderPane(this)
                        while(task.status != TaskStatus.IN_WORK){
                            sleep(1000) // sleep 1s
                            task.timeInvestedSeconds++
                            updateCell(threadCell, task)
                        }
                    }.start()
                }
            }

        else if(event.isSecondaryButtonDown){
            // open custom dialog
            (event.target as BorderPane).apply {
                tasks[tasks.getCellByBorderPane(this)] = TaskDialog.showDialog(tasks.getTaskByBorderPane(this)!!)
                updateCell(tasks.getCellByBorderPane(this), tasks.getTaskByBorderPane(this)!!)
            }
            mark(event)
        }
    }

    fun updateCell(cellContainer: TaskGridCellContainer, task: IvyLeeTask) {
        println("updating cell with task: $task")

        cellContainer.titleLabel.text = task.name
        cellContainer.descLabel.text = task.descr
        cellContainer.timeLabel.text = "${task.estTimeSeconds / 60.0} m"

        cellContainer.progressBar.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        cellContainer.statusIndicator.progress = task.timeInvestedSeconds * 1.0 / task.estTimeSeconds
        if(task.status == TaskStatus.DONE) cellContainer.statusIndicator.progress = 1.0

        if (task.timeInvestedSeconds >= task.estTimeSeconds)
            cellContainer.progressBarAdditional.progress = (task.timeInvestedSeconds - task.estTimeSeconds) * 1.0 / task.estTimeSeconds
        else
            cellContainer.progressBarAdditional.progress = 0.0

        cellContainer.borderPane.style {
            backgroundColor += task.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }
}
