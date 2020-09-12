package de.ott.ivy.ui.dialog

import de.ott.ivy.Entrypoint
import javafx.beans.property.StringProperty
import javafx.scene.Scene
import javafx.scene.control.Pagination
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View
import tornadofx.doubleBinding
import tornadofx.imageview

class SetupDialog : View("Setup") {

    override val root: BorderPane by fxml("/views/SetupDialog.fxml")

    val borderPaneGDrive: BorderPane by fxid("bp_gdrive")
    val borderPaneLanguage: BorderPane by fxid("bp_language")

    companion object {
        fun showDialog(){
            Stage().apply {
                scene = Scene(SetupDialog().root)
            }.showAndWait()
        }
    }

    init {
        title = "Ivy-Lee Task Tracker - Setup"
        icon = imageview(Image(Entrypoint::class.java.getResourceAsStream("/de/ott/ivy/images/frog-hq.png")))
    }

}