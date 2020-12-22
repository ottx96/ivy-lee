package com.github.ottx96.ivy.data

import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView

data class TaskCellContainer(var borderPane: BorderPane, var titleLabel: Label,
                             var deleteButton: ImageView, var expandButton: ImageView,
                             var descLabel: WebView, var timeLabel: Label, var progressBar: ProgressBar,
                             var progressBarAdditional: ProgressBar, var statusIndicator: ProgressIndicator)