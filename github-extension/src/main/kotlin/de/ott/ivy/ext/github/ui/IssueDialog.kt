package de.ott.ivy.ext.github.ui

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View

class IssueDialog: View("GitHub Issues"){
    override val root: BorderPane by fxml("/de/ott/ivy/ext/github/views/IssueDialog.fxml")

    companion object {
        fun showDialog(): Boolean{
            val dialog = IssueDialog()
            Stage().apply {
                scene = Scene(dialog.root)
            }.showAndWait()
            return true
        }
    }

    init {

    }

}