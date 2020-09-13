package de.ott.ivy.ui.dialog

import de.ott.ivy.Entrypoint
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValueBase
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*
import java.util.*

class SetupDialog : View("Setup") {

    override val root: BorderPane by fxml("/views/SetupDialog.fxml")

    val languages: ComboBox<String> by fxid("languages")
    val language: Label by fxid("language")

    val hyperlinkConnect: Hyperlink by fxid("hyperlink_connect")
    val progressGDrive: ProgressIndicator by fxid("progress_gdrive")

    val comboBoxTaskID: ComboBox<String> by fxid("combobox_task_id")

    val buttonOK: Button by fxid("btn_ok")
    val buttonCancel: Button by fxid("btn_cancel")

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

        buttonOK.enableWhen {
            comboBoxTaskID.valueProperty().isNotBlank()
                    .and(languages.valueProperty().isNotBlank())
                    .and(progressGDrive.indeterminateProperty().not())
        }
        buttonOK.disableWhen {
            comboBoxTaskID.valueProperty().isBlank()
                    .or(languages.valueProperty().isBlank())
                    .or(progressGDrive.indeterminateProperty())
        }

        hyperlinkConnect.onAction = EventHandler {
            progressGDrive.progress = 1.0
        }

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