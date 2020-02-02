package de.mait.ott

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import tornadofx.CssBox
import tornadofx.View
import tornadofx.style
import java.awt.event.MouseEvent

class TaskDialog: View("Task Dialog"){
    override val root: BorderPane by fxml("/views/TaskDialog.fxml")

    val taskDesc: TextArea by fxid("task_description")
    val taskName: TextField by fxid("task_name")

    val time: Slider by fxid("time")
    val lbl_time: Label by fxid("lbl_time")
    val tb_frog: ToggleButton by fxid("frog")
    val progress: ProgressIndicator by fxid("progress")

    val delete: Button by fxid("delete")
    val COLOR_DELETE = Color.valueOf("#e3736b")

    init {
        delete.onMouseEntered = EventHandler{
            delete.style {
                backgroundColor += COLOR_DELETE.desaturate()
                borderColor += CssBox(Color.valueOf("#cc0000"), Color.valueOf("#cc0000"), Color.valueOf("#cc0000"), Color.valueOf("#cc0000"))
            }
        }
        delete.onMouseExited = EventHandler {
            delete.style {
                backgroundColor += COLOR_DELETE
                borderColor += CssBox(Color.valueOf("#cc0000"), Color.valueOf("#cc0000"), Color.valueOf("#cc0000"), Color.valueOf("#cc0000"))
            }
        }

        lbl_time.textProperty().bind(time.valueProperty().asString("%.0f m"))

        with(currTask!!){
            taskDesc.text = descr
            taskName.text = name
            time.value = estTime.toDouble()
            tb_frog.isSelected = frog
            progress.progress = if(estTime > 0) timeInvestedMin.toDouble() / estTime.toDouble() else 0.0
        }
    }


    companion object {
        var currTask: IvyLeeTask? = null

        fun showDialog(task: IvyLeeTask): IvyLeeTask {
            currTask = task

            Stage().apply {
                scene = Scene(TaskDialog().root)
            }.showAndWait()

            return currTask!!
        }
    }

    fun ok() {
        currTask!!.apply {
            name = taskName.text
            descr = taskDesc.text
            estTime = time.value.toInt()
            frog = tb_frog.isSelected
            status = TaskStatus.UNDONE
            if(taskName.text.isBlank()) currTask!!.status = TaskStatus.EMPTY
        }
        close()
    }

    fun cancel() {
        close()
    }

    fun delete(){
        currTask = IvyLeeTask()
        close()
    }

}