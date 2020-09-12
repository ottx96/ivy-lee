package de.ott.ivy.ui.dialog

import de.ott.ivy.Entrypoint
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.View
import tornadofx.imageview
import java.util.*

class SetupDialog : View("Setup") {

    override val root: BorderPane by fxml("/views/SetupDialog.fxml")

    val borderPaneGDrive: BorderPane by fxid("bp_gdrive")
    val borderPaneLanguage: BorderPane by fxid("bp_language")

    val languages: ComboBox<String> by fxid("languages")
    val language: Label by fxid("language")

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

        Locale.getAvailableLocales().sortedBy { it.displayLanguage }.forEach {
            if(!languages.items.contains(it.displayLanguage))
                languages.items.add( it.displayLanguage )
        }
        languages.value = Locale.getDefault().displayLanguage
        language.text = Locale.getDefault().language

        languages.onAction = EventHandler {
            language.text =  Locale.getAvailableLocales().firstOrNull {  it.displayLanguage == languages.value  }?.language?:"N/A"
        }
    }

}