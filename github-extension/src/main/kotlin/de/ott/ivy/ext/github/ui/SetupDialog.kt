package de.ott.ivy.ext.github.ui

import de.ott.ivy.ext.github.config.Credentials
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View

class SetupDialog: View("") {
    override val root: BorderPane by fxml("")

    var credentials: Credentials? = null

    companion object {
        fun showDialog(): Credentials {
            val dialog = SetupDialog()
            Stage().apply {
                scene = Scene(dialog.root)
            }.show()
            return dialog.credentials!!
        }
    }
}