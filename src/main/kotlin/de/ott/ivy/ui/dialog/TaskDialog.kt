package de.ott.ivy.ui.dialog

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.enum.TaskStatus
import de.ott.ivy.html.MarkdownParser
import de.ott.ivy.ui.IvyLee
import fr.brouillard.oss.commonmark.ext.notifications.NotificationsExtension
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import javafx.stage.Stage
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import tornadofx.CssBox
import tornadofx.View
import tornadofx.style
import java.lang.Thread.sleep


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
            taskName.text = name
            updateHeader()
            taskDesc.text = descr
            updateWebView()
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
        if(webView.engine.userStyleSheetLocation.isNullOrBlank())
            webView.engine.userStyleSheetLocation = javaClass.getResource("/de/ott/ivy/css/style.css").toString()

        webView.engine.loadContent(MarkdownParser.convertHtml(taskDesc.text), "text/html")
    }

}