package de.ott.ivy.ui.overview.impl

import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.TaskCellContainer
import de.ott.ivy.ui.overview.IvyLee
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
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

        var title: Label? = null
        var desc: WebView? = null
        var time: Label? = null
        var status: ProgressIndicator? = null
        var progress: ProgressBar? = null
        var progressAdditional: ProgressBar? = null
        bp.children.forEach { bpChild ->
            println("curr child: $bpChild")
            when (bpChild) {
                is Label -> title = bpChild
                is WebView -> desc = bpChild
                is HBox -> {
                    bpChild.children.forEach { hbChild ->
                        when (hbChild) {
                            is ProgressBar -> {
                                if (hbChild.id.matches(Regex(""".*additional.*""")))
                                    progressAdditional = hbChild
                                else progress = hbChild
                            }
                            is Label -> time = hbChild
                            is ProgressIndicator -> status = hbChild
                        }
                    }
                }
            }
        }

        return TaskCellContainer(bp, title!!, desc!!, time!!, progress!!, progressAdditional!!, status!!) to bp
    }

}