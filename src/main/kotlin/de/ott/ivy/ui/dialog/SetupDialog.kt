package de.ott.ivy.ui.dialog

import de.ott.ivy.data.IvyLeeTask
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.DialogPane
import javafx.scene.control.Pagination
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import tornadofx.View
import tornadofx.borderpane
import tornadofx.center
import tornadofx.label

class SetupDialog : View("Setup") {

    override val root: AnchorPane by fxml("/views/SetupDialog.fxml")

    val pagination: Pagination by fxid("pagination")

    companion object {
        fun showDialog(){
            Stage().apply {
                scene = Scene(SetupDialog().root)
            }.showAndWait()
        }
    }

    init {
        pagination.setPageFactory {
            borderpane {
                center {
                    label("this is page $it") {  }
                }
            }
        }
        pagination.maxPageIndicatorCount = 50
    }

}