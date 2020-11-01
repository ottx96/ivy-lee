package de.ott.ivy.ui.overview.impl

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.ui.overview.IvyLee
import io.opencensus.common.Functions
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.web.WebView

object ComponentBuilder {

    fun createTaskContainer(task: IvyLeeTask): Pair<TaskCellContainer, BorderPane> {
        println("Create borderPane for task: $task")
        val bp = FXMLLoader().apply {
            location = classLoader.getResource("views/TaskCell.fxml")
        }.load<BorderPane>()

        val title: Label = (bp.top as BorderPane).center as Label
        val buttonDelete: ImageView = ((bp.top as BorderPane).left as HBox).children.first { it.id == "delete" } as ImageView
        val deletionOk: ImageView = ((bp.top as BorderPane).left as HBox).children.first { it.id == "delete_ok" } as ImageView
        val deletionCancel: ImageView = ((bp.top as BorderPane).left as HBox).children.first { it.id == "delete_cancel" } as ImageView

        // set invisible, show when pressing delete
        deletionOk.isVisible = false
        deletionCancel.isVisible = false

        val buttonExpand: ImageView = ((bp.top as BorderPane).right as HBox).children.first { it.id == "expand" } as ImageView
        val desc: WebView = bp.center as WebView
        val time: Label = (bp.bottom as HBox).children.first { it is Label } as Label
        val status: ProgressIndicator = (bp.bottom as HBox).children.first {  it is ProgressIndicator } as ProgressIndicator
        val progress: ProgressBar = (bp.bottom as HBox).children.first { it.id == "progress_middle_right" } as ProgressBar
        val progressAdditional: ProgressBar = (bp.bottom as HBox).children.first { it.id == "progress_additional_middle_right" } as ProgressBar

        return TaskCellContainer(bp, title, buttonDelete, buttonExpand, desc, time, progress, progressAdditional, status) to bp
    }

}