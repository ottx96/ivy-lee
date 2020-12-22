package com.github.ottx96.ivy.ext.github.ui

import com.google.gson.GsonBuilder
import com.github.ottx96.ivy.ext.github.GithubExtension
import com.github.ottx96.ivy.ext.github.config.Credentials
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.DialogPane
import javafx.scene.control.Hyperlink
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.kohsuke.github.GitHub
import tornadofx.View
import java.awt.Desktop
import java.net.URI

class SetupDialog: View("") {
    override val root: DialogPane by fxml("/views/SetupDialog.fxml")

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

    var g: GitHub? = null
    init {

        imageView.image = Image("/views/images/Red_x_100x100.png")
        create.onAction = EventHandler {
            Desktop.getDesktop().browse(URI.create("https://github.com/settings/tokens/new"))
        }

        buttonOk.onAction = EventHandler {
            if(g?.isCredentialValid == true){
                // write json file
                Credentials(username.text, token.text).toJson(GithubExtension.CREDENTIALS_FILE)
                close()
            }

            if(!(g?.isCredentialValid?:false)){
                buttonOk.isDisable = true
                g = GitHub.connect(username.text, token.text)
                buttonOk.isDisable = false
                if(g?.isCredentialValid == true){
                    buttonOk.text = "OK"
                    imageView.image = Image("/views/images/Light_green_check_100x100.png")
                    username.isDisable = true
                    token.isDisable = true
                }else{
                    imageView.image = Image("/views/images/Red_x_100x100.png")
                }
            }
        }

        buttonCancel.onAction = EventHandler {
            close()
        }

    }

}