package de.ott.ivy.ext.github.ui

import de.ott.ivy.ext.github.config.Credentials
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.DialogPane
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import tornadofx.View
import tornadofx.enableWhen

class SetupDialog: View("") {
    override val root: DialogPane by fxml("/de/ott/ivy/ext/github/views/SetupDialog.fxml")

    var credentials: Credentials? = null

    val buttonOk: Button by fxid("ok")
    val buttonCancel: Button by fxid("cancel")
    val imageView: ImageView by fxid("imageview")
    val create: Hyperlink by fxid("create")
    val username: TextField by fxid("username")
    val token: TextField by fxid("token")

    companion object {
        fun showDialog(): Credentials {
            val dialog = SetupDialog()
            Stage().apply {
                scene = Scene(dialog.root)
            }.showAndWait()
            return dialog.credentials!!
        }
    }

    init {
        imageView.image = Image("/de/ott/ivy/ext/github/views/images/Red_x_100x100.png")
        println(imageView.image)
    }

}