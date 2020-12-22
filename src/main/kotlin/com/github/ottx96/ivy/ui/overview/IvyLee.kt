package com.github.ottx96.ivy.ui.overview

import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.data.TaskCellContainer
import com.github.ottx96.ivy.gdrive.ConnectionProvider
import com.github.ottx96.ivy.gdrive.RemoteFilesHandler
import com.github.ottx96.ivy.ui.overview.event.IvyLeeEventHandler
import com.github.ottx96.ivy.ui.overview.impl.IvyLeeDecorator
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import kotlinx.serialization.ExperimentalSerializationApi
import tornadofx.View


/**
 * Main program (UI)
 * Project: ivy-lee
 * Package:
 * Created: 28.01.2020 15:55
 * @author = manuel.ott
 * @since = 28. Januar 2020
 */
@ExperimentalSerializationApi
class IvyLee : View("Ivy-Lee Tracking") {
    override val root: ScrollPane by fxml("/views/IvyLee.fxml")

    private val decorator = IvyLeeDecorator(this)

    companion object {
        const val MAIN_THREAD_NAME = "UI_THREAD"
        val tasks = mapOf<TaskCellContainer, IvyLeeTask>().toMutableMap()

        val remoteFilesHandler = RemoteFilesHandler(ConnectionProvider.connect())

        fun Map<TaskCellContainer, IvyLeeTask>.getCellByBorderPane(bp: BorderPane?) = keys.first { it.borderPane == bp!! }
        fun Map<TaskCellContainer, IvyLeeTask>.getTaskByBorderPane(bp: BorderPane?) = tasks[keys.first { it.borderPane == bp!! }]
    }

    val anchorPane: AnchorPane by fxid("anchor_pane")
    val taskList: VBox by fxid("tasklist")
    val toolBar: HBox by fxid("tool_bar")
    val addButton: ImageView by fxid("add")
    val addButtonPane: Pane by fxid("pane_add")
    val settingsButton: ImageView by fxid("settings")

    internal val eventHandler = IvyLeeEventHandler(taskList)

    init {
        Thread.currentThread().name = MAIN_THREAD_NAME
        decorator.initializeUI()
        decorator.startBackgroundTasks()
    }

}
