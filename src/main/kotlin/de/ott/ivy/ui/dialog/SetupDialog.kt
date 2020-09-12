package de.ott.ivy.ui.dialog

import de.ott.ivy.data.IvyLeeTask
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.DialogPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
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

}