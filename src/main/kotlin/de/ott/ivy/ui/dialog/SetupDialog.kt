package de.ott.ivy.ui.dialog

import javafx.scene.Scene
import javafx.scene.control.Pagination
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View

class SetupDialog : View("Setup") {

    override val root: BorderPane by fxml("/views/SetupDialog.fxml")

    companion object {
        fun showDialog(){
            Stage().apply {
                scene = Scene(SetupDialog().root)
            }.showAndWait()
        }
    }

    init {

    }

}