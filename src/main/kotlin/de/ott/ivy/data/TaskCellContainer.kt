package de.ott.ivy.data

import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView

data class TaskCellContainer(var borderPane: BorderPane, var titleLabel: Label,
                             var descLabel: WebView, var timeLabel: Label, var progressBar: ProgressBar,
                             var progressBarAdditional: ProgressBar, var statusIndicator: ProgressIndicator)