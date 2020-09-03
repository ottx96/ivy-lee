package de.ott.ivy.data

import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane

data class TaskGridCellContainer(var borderPane: BorderPane, var titleLabel: Label, var descLabel: TextArea, var timeLabel: Label, var progressBar: ProgressBar, var progressBarAdditional: ProgressBar, var statusIndicator: ProgressIndicator): Comparable<TaskGridCellContainer>{
    override fun compareTo(other: TaskGridCellContainer): Int {
        return borderPane.id.compareTo(other.borderPane.id)
    }
}