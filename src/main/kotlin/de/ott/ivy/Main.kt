package de.ott.ivy

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.serialization.*
import kotlinx.serialization.cbor.Cbor
import tornadofx.*
import java.io.File
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * TODO: Insert Description!
 * Project: ivy-lee
 * Package:
 * Created: 28.01.2020 15:55
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
class IvyLee : View("Ivy-Lee Tracking") {
    override val root: GridPane by fxml("/views/IvyLee.fxml")

    companion object{
        val tasks = mapOf<TaskGridCell, IvyLeeTask>().toSortedMap()
        fun Map<TaskGridCell, IvyLeeTask>.getCellByBorderPane(bp: BorderPane?) =  keys.first { it.borderPane == bp!! }
        fun Map<TaskGridCell, IvyLeeTask>.getTaskByBorderPane(bp: BorderPane?) =  tasks[keys.first { it.borderPane == bp!! }]
    }

    val bpTopLeft: BorderPane by fxid("bp_topleft")
    val bpTopRight: BorderPane by fxid("bp_topright")
    val bpMiddleLeft: BorderPane by fxid("bp_middleleft")
    val bpMiddleRight: BorderPane by fxid("bp_middleright")
    val bpBottomLeft: BorderPane by fxid("bp_bottomleft")
    val bpBottomRight: BorderPane by fxid("bp_bottomright")

    init{
        println("loading tasks!")
        val folder = File("./IvyLeeTasks/")
        var oldTasks: List<IvyLeeTask?>? = null
        try{
            val lastFile = folder.listFiles()?.toMutableList()?.sortedBy { -1 * it.lastModified() }?.first()
            println("loading from file ${lastFile?.absolutePath?:"NONE"}")
            oldTasks = Cbor.plain.load<List<IvyLeeTask>>(IvyLeeTask.serializer().list, lastFile?.readBytes()?:throw Exception("No data stored"))
            oldTasks.forEach ( ::println )
        }catch(e: Exception){}

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
            tasks[TaskGridCell(bp.first, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!)] = bp.second ?: IvyLeeTask()
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
                    TaskStatus.EMPTY -> {}
                    TaskStatus.UNDONE -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.IN_WORK
                    TaskStatus.IN_WORK -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.DONE
                    TaskStatus.DONE -> tasks.getTaskByBorderPane(this)!!.status = TaskStatus.UNDONE
                }
                updateCell(tasks.getCellByBorderPane(this), task)
                if(task.status == TaskStatus.IN_WORK){
                    Thread{
                        val threadTask = task
                        val threadCell = tasks.getCellByBorderPane(this)
                        while(true){
                            repeat(60){
                                if(threadTask.status != TaskStatus.IN_WORK) return@Thread
                                sleep(1000)
                            }
                            threadTask.timeInvestedMin++
                            updateCell(threadCell, threadTask)
                        }
                    }.start()
                }
            }

        if(event.isSecondaryButtonDown){
            // open custom dialog
            (event.target as BorderPane).apply {
                tasks[tasks.getCellByBorderPane(this)] = TaskDialog.showDialog(tasks.getTaskByBorderPane(this)!!)
                updateCell(tasks.getCellByBorderPane(this), tasks.getTaskByBorderPane(this)!!)
            }
            mark(event)
        }
    }

    fun updateCell(cell: TaskGridCell, task: IvyLeeTask) {
        println("updating cell with task: $task")

        cell.titleLabel.text = task.name
        cell.descLabel.text = task.descr
        cell.timeLabel.text = "${task.estTime} m"

        cell.progressBar.progress = task.timeInvestedMin * 1.0 / task.estTime
        cell.statusIndicator.progress = task.timeInvestedMin * 1.0 / task.estTime
        if(task.status == TaskStatus.DONE) cell.statusIndicator.progress = 1.0

        if (task.timeInvestedMin >= task.estTime)
            cell.progressBarAdditional.progress = (task.timeInvestedMin - task.estTime) * 1.0 / task.estTime
        else
            cell.progressBarAdditional.progress = 0.0

        cell.borderPane.style {
            backgroundColor += task.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }
}

class Main: App(IvyLee::class){

    @UseExperimental(ImplicitReflectionSerializer::class)
    override fun start(stage: Stage) {
        stage.onCloseRequest = EventHandler {
            val folder = File("./IvyLeeTasks/")
            folder.mkdirs()
            val nFile =
                File(folder.absolutePath + "/tasks.${LocalDateTime.now().format(DateTimeFormatter.ofPattern("""yyyy-MM-dd_hh-mm"""))}.db")
            if (nFile.exists())
                nFile.renameTo(File(nFile.absolutePath + ".old"))

            //Speichern auf nFile
            println("Exit..")
            println("dumping tasks..")
            nFile.writeBytes(Cbor.plain.dump(IvyLeeTask.serializer().list, IvyLee.tasks.values.toList()))
        }
        super.start(stage)
    }
}