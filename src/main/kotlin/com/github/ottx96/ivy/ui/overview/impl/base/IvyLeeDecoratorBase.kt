package com.github.ottx96.ivy.ui.overview.impl.base

import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.ui.overview.IvyLee
import com.github.ottx96.ivy.ui.overview.impl.ComponentBuilder
import com.github.ottx96.ivy.ui.overview.impl.TaskCellUpdater
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.minus
import java.io.File
import java.util.*
import kotlin.math.max
import kotlin.math.min

@ExperimentalSerializationApi
open class IvyLeeDecoratorBase(private val base: IvyLee) {

    protected fun initializeUIElements() {
        setScrollSpeed()
        base.addButton.onMouseClicked = createAdderHandler()
        base.addButtonPane.prefWidthProperty().bind(base.root.widthProperty())
        base.addButton.xProperty().bind(base.addButtonPane.widthProperty().minus(base.addButton.fitWidthProperty()).minus(15))
    }

    protected fun setInitialSize() {
        when (IvyLee.tasks.size) {
            in 1..3 -> IvyLee.tasks.size * 200.0
            else -> base.root.prefHeight = 800.0
        }
    }

    protected fun populateTaskCells() {
        val oldTasks: List<IvyLeeTask> = populateTasks()
        base.taskList.children.removeAll { it is BorderPane }
        oldTasks.forEach { createTaskCell(oldTasks, it) }
    }

    private fun createTaskCell(loadedTasks: List<IvyLeeTask>, task: IvyLeeTask) {
        val built = ComponentBuilder.createTaskContainer(task)
        val bp = built.second

        base.taskList.children.add(bp)
        IvyLee.tasks[built.first] = task

        bp.minWidthProperty().bind(base.root.widthProperty().minus(15))
        bp.minHeightProperty().bind(base.root.heightProperty().minus(base.toolBar.heightProperty())
            .divide(min(4, max(1, loadedTasks.size)))
            .minus(1.35))  //  border with is ~2 px

        bp.onMouseEntered = EventHandler { base.eventHandler.mark(it) }
        bp.onMouseExited = EventHandler { base.eventHandler.unmark(it) }
        bp.onMousePressed = EventHandler { base.eventHandler.onClick(it) }

        IvyLee.tasks.forEach(::println)
        TaskCellUpdater.updateTaskCell(task, built.first)
    }

    private fun populateTasks(): List<IvyLeeTask> {
        val tasksFile = File("tasks.db")
        try {
            println("downloading tasks from gdrive")
            IvyLee.remoteFilesHandler.readTasks(tasksFile)
            println("loading tasks from file ${tasksFile.absolutePath ?: "NONE"}")
            val readTasks = Cbor { ignoreUnknownKeys = true }.decodeFromByteArray(
                ListSerializer(IvyLeeTask.serializer()),
                tasksFile.readBytes())
            readTasks.forEach(::println)
            return readTasks
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Collections.emptyList()
    }

    private fun createAdderHandler(): EventHandler<MouseEvent> = EventHandler {
        val newTask = IvyLeeTask()
        val taskBuilt = ComponentBuilder.createTaskContainer(newTask)
        base.taskList.children.add(taskBuilt.second)
        IvyLee.tasks[taskBuilt.first] = newTask

        IvyLee.tasks.forEach { task ->
            task.key.borderPane.apply {
                minHeightProperty().unbind()
                minWidthProperty().unbind()
                minWidthProperty().bind(base.root.widthProperty().minus(15))
                minHeightProperty().bind(base.root.heightProperty().minus(base.toolBar.heightProperty())
                    .divide(min(4, max(1, IvyLee.tasks.size)))          //  if less than 4 tasks, scale - scale for 4 tasks max, then scroll for other tasks
                    .minus(1.35))                                //  border width is ~2 px

                onMouseEntered = EventHandler { base.eventHandler.mark(it) }
                onMouseExited = EventHandler { base.eventHandler.unmark(it) }
                onMousePressed = EventHandler { base.eventHandler.onClick(it) }
            }
        }

        TaskCellUpdater.updateTaskCell(newTask, taskBuilt.first)
    }

    private fun setScrollSpeed() {
        // set scroll speed
        val scrollSpeed = 0.00175
        base.root.content.onScroll = EventHandler {
            base.root.vvalue -= it.deltaY * scrollSpeed
        }
    }

}