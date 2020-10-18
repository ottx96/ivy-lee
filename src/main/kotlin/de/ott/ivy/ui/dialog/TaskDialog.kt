package de.ott.ivy.ui.dialog

import de.ott.ivy.Entrypoint
import de.ott.ivy.TaskExtension
import de.ott.ivy.annotation.Extension
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.enums.Priorities
import de.ott.ivy.data.enums.TaskStatus
import de.ott.ivy.html.MarkdownParser
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.PixelReader
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import javafx.stage.Stage
import kotlinx.serialization.ExperimentalSerializationApi
import tornadofx.View
import java.io.File


@ExperimentalSerializationApi
class TaskDialog : View("Task Dialog"){

    companion object {
        private val COLOR_BORDER = Color.valueOf("#cc0000")
        var currTask: IvyLeeTask? = null

        fun showDialog(task: IvyLeeTask): IvyLeeTask {
            currTask = task
            Stage().apply {
                scene = Scene(TaskDialog().root)
            }.showAndWait()
            return currTask!!
        }
    }

    override val root: BorderPane by fxml("/views/TaskDialog.fxml")

    val taskName: TextField by fxid("task_name")
    val taskDesc: TextArea by fxid("task_description")
    val webView: WebView by fxid("webview")
    val extensionsButton: SplitMenuButton by fxid("extensions")
    val extensionClassList = mutableMapOf<String, Class<*>>()

    val priorityLowest: ImageView by fxid("priority_lowest")
    val priorityLow: ImageView by fxid("priority_low")
    val priorityMedium: ImageView by fxid("priority_medium")
    val priorityHigh: ImageView by fxid("priority_high")
    val priorityHighest: ImageView by fxid("priority_highest")
    val priorityCritical: ImageView by fxid("priority_critical")

    init {
        initUIFromTask(currTask!!)

        priorityLowest.image = colorize(priorityLowest.image, Color.BLACK, Priorities.LOWEST.color)
        priorityLow.image = colorize(priorityLow.image, Color.BLACK, Priorities.LOW.color)
        priorityMedium.image = colorize(priorityMedium.image, Color.BLACK, Priorities.MEDIUM.color)
        priorityHigh.image = colorize(priorityHigh.image, Color.BLACK, Priorities.HIGH.color)
        priorityHighest.image = colorize(priorityHighest.image, Color.BLACK, Priorities.HIGHEST.color)
        priorityCritical.image = colorize(priorityCritical.image, Color.BLACK, Priorities.CRITICAL.color)

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
        }
    }

    private fun initUIFromTask(task: IvyLeeTask) {
        with(task) {
            taskName.text = name
            updateHeader()
            taskDesc.text = descr
            updateWebView()
        }
    }

    private fun updateTask(task: IvyLeeTask): IvyLeeTask {
        return task.apply {
            name = taskName.text
            descr = taskDesc.text
            status = TaskStatus.UNDONE
            if(taskName.text.isBlank())
                currTask!!.status = TaskStatus.EMPTY
        }
    }

    fun ok() {
        updateTask(currTask!!)
        close()
    }

    fun cancel() {
        close()
    }

    fun updateHeader() {
//        header.text = if(taskName.text.isBlank()) "Create Task" else taskName.text
    }

    fun updateWebView(){
        if(webView.engine.userStyleSheetLocation.isNullOrBlank())
            webView.engine.userStyleSheetLocation = javaClass.getResource("/de/ott/ivy/css/style.css").toString()

        webView.engine.loadContent(MarkdownParser.convertHtml(taskDesc.text), "text/html")
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

}