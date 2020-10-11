package de.ott.ivy.ui.event

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.data.enum.TaskStatus
import de.ott.ivy.html.MarkdownParser
import de.ott.ivy.ui.IvyLee
import de.ott.ivy.ui.IvyLee.Companion.getCellByBorderPane
import de.ott.ivy.ui.IvyLee.Companion.getTaskByBorderPane
import de.ott.ivy.ui.dialog.TaskDialog
import javafx.scene.control.ToolBar
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.CssBox
import tornadofx.style

class IvyLeeEventHandler(val anchorPane: AnchorPane, val taskList: VBox, val toolBar: ToolBar, val addButton: ImageView) {

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
                updateCell(IvyLee.tasks.getCellByBorderPane(this), task)
                if (task.status == TaskStatus.IN_WORK) {
                    // stop other timers
                    IvyLee.tasks.values.filter { it.status == TaskStatus.IN_WORK && it != task }.forEach {
                        it.status = TaskStatus.UNDONE
                    }
                    Thread {
                        val threadCell = IvyLee.tasks.getCellByBorderPane(this)
                        while (task.status == TaskStatus.IN_WORK) {
                            task.timeInvestedSeconds++
                            updateCell(threadCell, task)
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
                    updateCell(IvyLee.tasks.getCellByBorderPane(this), IvyLee.tasks.getTaskByBorderPane(this)!!)
                    mark(event)
                }
            }
        }
    }

    fun updateCell(cellContainer: TaskCellContainer, task: IvyLeeTask) {
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

        if (Thread.currentThread().name == IvyLee.MAIN_THREAD_NAME)
            cellContainer.descLabel.engine.loadContent(MarkdownParser.convertHtml(task.descr), "text/html")
    }

}