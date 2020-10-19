package de.ott.ivy.ui.dialog

import de.ott.ivy.Entrypoint
import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.data.enums.Priorities
import de.ott.ivy.data.enums.TaskStatus
import de.ott.ivy.ui.overview.impl.ComponentBuilder
import de.ott.ivy.ui.overview.impl.TaskCellUpdater
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.serialization.ExperimentalSerializationApi
import tornadofx.View
import java.io.File


@ExperimentalSerializationApi
class TaskDialog : View("Task Dialog"){

    companion object {
        private val COLOR_BORDER = Color.valueOf("#cc0000")
        var currTask: IvyLeeTask? = null
        var nTask: IvyLeeTask? = null

        fun showDialog(task: IvyLeeTask): IvyLeeTask {
            nTask = task
            currTask = task.copy()
            Stage().apply {
                scene = Scene(TaskDialog().root)
            }.showAndWait()
            return nTask!!
        }
    }

    override val root: BorderPane by fxml("/views/TaskDialog.fxml")

    val vbox: VBox by fxid("vbox")
    val taskName: TextField by fxid("task_name")
    val taskDesc: TextArea by fxid("task_description")
    val extensionsButton: SplitMenuButton by fxid("extensions")
    val extensionClassList = mutableMapOf<String, Class<*>>()
    val labelPriority: Label by fxid("lbl_priority")
    val dateChooser: DatePicker by fxid("due_date")

    val priorityLowest: ImageView by fxid("priority_lowest")
    val priorityLow: ImageView by fxid("priority_low")
    val priorityMedium: ImageView by fxid("priority_medium")
    val priorityHigh: ImageView by fxid("priority_high")
    val priorityHighest: ImageView by fxid("priority_highest")
    val priorityCritical: ImageView by fxid("priority_critical")

    val prioritiesMapping = listOf(priorityLowest, priorityLow, priorityMedium, priorityHigh, priorityHighest, priorityCritical )
                              .zip(listOf(Priorities.LOWEST, Priorities.LOW, Priorities.MEDIUM, Priorities.HIGH, Priorities.HIGHEST, Priorities.CRITICAL )).toMap()

    val taskComponents: TaskCellContainer

    init {
        val taskBuilt = ComponentBuilder.createTaskContainer(currTask!!)
        taskComponents = taskBuilt.first
        initUIFromTask(currTask!!)
        TaskCellUpdater.updateTaskCell(currTask!!, taskComponents)

        vbox.children.add(0, taskBuilt.second)
        taskBuilt.second.prefHeightProperty().bind( vbox.heightProperty().divide(2) )

        prioritiesMapping.forEach {
            it.component1().setOnMouseEntered { evt ->
                updatePriorityIndicators(prioritiesMapping[evt.target]!!)
            }
            it.component1().onMouseClicked = EventHandler { evt ->
                currTask!!.priority = prioritiesMapping[evt.target]!!
            }
            it.component1().setOnMouseExited {evt ->
                updatePriorityIndicators(currTask!!.priority)
            }
        }

        File(Entrypoint::class.java.classLoader.getResource("de/ott/ivy/extension/extensions.txt")!!.file).useLines { lines ->
            lines.filter(String::isNotBlank).forEach {
                try{
                    val cl = Class.forName(it)
                    if(!cl.interfaces.any { it == TaskExtension::class.java }) return@forEach
                    extensionClassList[cl.getAnnotation(Extension::class.java)?.displayString ?: cl.simpleName] = cl
                    extensionsButton.items.add(
                            MenuItem(cl.getAnnotation(Extension::class.java)?.displayString ?: cl.simpleName).apply {
                                onAction = EventHandler { event ->
                                    extensionsButton.text = (event.target as MenuItem).text
                                }
                            }
                    )
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        extensionsButton.text = extensionsButton.items[0].text
        extensionsButton.onAction = EventHandler {
            println("starting extension")
            val cl = extensionClassList[extensionsButton.text]!!
            val inst = cl.declaredConstructors[0].newInstance() as TaskExtension
            val newTask = currTask!!.copy()  // create copy of the task
            inst.execute(newTask)  // execute the extension
            initUIFromTask(newTask)  // refresh UI with new task
            TaskCellUpdater.updateTaskCell(newTask, taskComponents)
        }
    }

    private fun initUIFromTask(task: IvyLeeTask) {
        with(task) {
            taskName.text = name
            taskDesc.text = descr
            labelPriority.text = priority.label
            dateChooser.value = dueDate
            updatePriorityIndicators(priority)
        }
    }

    private fun updateTask(task: IvyLeeTask): IvyLeeTask {
        return task.apply {
            name = taskName.text
            descr = taskDesc.text

            dueDate = dateChooser.value
            status = TaskStatus.UNDONE
            priority = currTask!!.priority

            if(taskName.text.isBlank())
                status = TaskStatus.EMPTY
        }
    }

    fun ok() {
        updateTask(nTask!!)
        close()
    }

    fun cancel() {
        close()
    }

    fun updateDisplayedTask() {
        updateTask(currTask!!)
        TaskCellUpdater.updateTaskCell(currTask!!, taskComponents)
    }

    private fun colorize(image: Image, old: Color, new: Color): Image {
        val result = WritableImage(image.width.toInt(), image.height.toInt())
        val writer = result.pixelWriter

        for(x in 0 until image.width.toInt())
            for(y in 0 until image.height.toInt())
                writer.setColor(x, y,
                        if(image.pixelReader.getColor(x, y) == old) new else image.pixelReader.getColor(x, y))

        return result
    }

    private fun updatePriorityIndicators(priority: Priorities){
        var flag = false
        prioritiesMapping.entries.reversed().forEach {
            if(priority == it.component2()) {
                flag = true
                labelPriority.text = it.component2().label
            }
            if(flag) it.component1().image = colorize(it.component1().image, Color.BLACK, it.component2().color)
            else it.component1().image = Image("/de/ott/ivy/images/priority_element.png")
        }
    }

}