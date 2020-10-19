package de.ott.ivy.ui.overview.event

import de.ott.ivy.data.enums.TaskStatus
import de.ott.ivy.ui.overview.IvyLee
import de.ott.ivy.ui.overview.IvyLee.Companion.getCellByBorderPane
import de.ott.ivy.ui.overview.IvyLee.Companion.getTaskByBorderPane
import de.ott.ivy.ui.dialog.TaskDialog
import de.ott.ivy.ui.overview.impl.TaskCellUpdater
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.CssBox
import tornadofx.style

class IvyLeeEventHandler(private val taskList: VBox) {

    fun mark(event: MouseEvent) {
        (event.target as BorderPane).style {
            borderColor += CssBox(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE)
            backgroundColor += IvyLee.tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color.desaturate()
        }
    }

    fun unmark(event: MouseEvent) {
        (event.target as BorderPane).style {
            backgroundColor += IvyLee.tasks.getTaskByBorderPane(event.target as BorderPane)!!.status.color
            borderColor += CssBox(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)
        }
    }

    fun onClick(event: MouseEvent) {
        if (event.isPrimaryButtonDown)
            (event.target as BorderPane).apply {
                val task = IvyLee.tasks.getTaskByBorderPane(this)!!
                when (task.status) {
                    TaskStatus.EMPTY -> {
                        return
                    }
                    TaskStatus.UNDONE -> task.status = TaskStatus.IN_WORK
                    TaskStatus.IN_WORK -> task.status = TaskStatus.DONE
                    TaskStatus.DONE -> task.status = TaskStatus.UNDONE
                }
                TaskCellUpdater.updateTaskCell(task, IvyLee.tasks.getCellByBorderPane(this))
                if (task.status == TaskStatus.IN_WORK) {
                    // stop other timers
                    IvyLee.tasks.values.filter { it.status == TaskStatus.IN_WORK && it != task }.forEach {
                        it.status = TaskStatus.UNDONE
                    }
                    Thread {
                        val threadCell = IvyLee.tasks.getCellByBorderPane(this)
                        while (task.status == TaskStatus.IN_WORK) {
                            task.timeInvestedSeconds++
                            TaskCellUpdater.updateTaskCell(task, threadCell)
                            Thread.sleep(1000) // sleep 1s
                        }
                    }.start()
                }
            }
        else {
            // open custom dialog
            (event.target as BorderPane).apply {
                val newTask = TaskDialog.showDialog(IvyLee.tasks.getTaskByBorderPane(this)!!)
                if (newTask.status == TaskStatus.EMPTY) {
                    val cell = IvyLee.tasks.getCellByBorderPane(this)
                    taskList.children.remove(cell.borderPane)
                    IvyLee.tasks.remove(cell)
                } else {
                    IvyLee.tasks[IvyLee.tasks.getCellByBorderPane(this)] = newTask
                    TaskCellUpdater.updateTaskCell(IvyLee.tasks.getTaskByBorderPane(this)!!, IvyLee.tasks.getCellByBorderPane(this))
                    mark(event)
                }
            }
        }
    }

}