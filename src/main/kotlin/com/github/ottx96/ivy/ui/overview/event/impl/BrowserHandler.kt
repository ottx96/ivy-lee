package com.github.ottx96.ivy.ui.overview.event.impl

import com.github.ottx96.ivy.data.enums.TaskStatus
import com.github.ottx96.ivy.ui.dialog.TaskDialog
import com.github.ottx96.ivy.ui.overview.IvyLee
import com.github.ottx96.ivy.ui.overview.IvyLee.Companion.getCellByBorderPane
import com.github.ottx96.ivy.ui.overview.IvyLee.Companion.getTaskByBorderPane
import com.github.ottx96.ivy.ui.overview.impl.TaskCellUpdater
import javafx.scene.layout.BorderPane
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
object BrowserHandler {

    fun onLeftClick(borderPane: BorderPane){
        borderPane.apply {
            val task = IvyLee.tasks.getTaskByBorderPane(this)!!
            when (task.status) {
                TaskStatus.EMPTY -> return
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
                        Thread.sleep(1000) // sleep 1 second
                    }
                }.start()
            }
        }
    }

    fun onRightClick(borderPane: BorderPane){
        // open custom dialog
        borderPane.apply {
            val newTask = TaskDialog.showDialog(IvyLee.tasks.getTaskByBorderPane(this)!!)
            IvyLee.tasks[IvyLee.tasks.getCellByBorderPane(this)] = newTask
            TaskCellUpdater.updateTaskCell(IvyLee.tasks.getTaskByBorderPane(this)!!, IvyLee.tasks.getCellByBorderPane(this))
        }
    }

}