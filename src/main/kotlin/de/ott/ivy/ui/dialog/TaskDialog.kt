package de.ott.ivy.ui.dialog

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.enum.TaskStatus
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import javafx.stage.Stage
import tornadofx.CssBox
import tornadofx.View
import tornadofx.style

class TaskDialog : View("Task Dialog"){
    private val COLOR_BORDER = Color.valueOf("#cc0000")

    override val root: BorderPane by fxml("/views/TaskDialog.fxml")

    val taskName: TextField by fxid("task_name")
    val header: Label by fxid("dialog_header")

    val taskDesc: TextArea by fxid("task_description")
    val webView: WebView by fxid("webview")

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
                borderColor += CssBox(COLOR_BORDER, COLOR_BORDER, COLOR_BORDER, COLOR_BORDER)
            }
        }
        delete.onMouseExited = EventHandler {
            delete.style {
                backgroundColor += COLOR_DELETE
                borderColor += CssBox(COLOR_BORDER, COLOR_BORDER, COLOR_BORDER, COLOR_BORDER)
            }
        }

        lbl_time.textProperty().bind(time.valueProperty().asString("%.0f m"))

        with(currTask!!){
            taskDesc.text = descr
            taskName.text = name
            updateHeader()
            time.value = estTimeSeconds.toDouble() / 60.0
            tb_frog.isSelected = frog
            progress.progress = if(estTimeSeconds > 0) timeInvestedSeconds.toDouble() / estTimeSeconds.toDouble() else 0.0
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
            estTimeSeconds = time.value.toInt() * 60
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

    fun updateHeader() {
        header.text = if(taskName.text.isBlank()) "Create Task" else taskName.text
    }

    fun updateWebView(){
        webView.engine.load("http://www.google.de")
    }

}