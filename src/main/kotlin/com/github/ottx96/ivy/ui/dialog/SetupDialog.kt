package com.github.ottx96.ivy.ui.dialog

import com.google.api.services.drive.Drive
import com.google.common.util.concurrent.Futures.withTimeout
import com.github.ottx96.ivy.Entrypoint
import com.github.ottx96.ivy.config.Configuration
import com.github.ottx96.ivy.gdrive.ConnectionProvider
import com.github.ottx96.ivy.gdrive.RemoteFilesHandler
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValueBase
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import javafx.stage.Stage
import tornadofx.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class SetupDialog : View("Setup") {
    override val root: BorderPane by fxml("/views/SetupDialog.fxml")

    val languages: ComboBox<String> by fxid("languages")
    val language: Label by fxid("language")

    val hyperlinkConnect: Hyperlink by fxid("hyperlink_connect")
    val progressGDrive: ProgressIndicator by fxid("progress_gdrive")

    val comboBoxTaskID: ComboBox<String> by fxid("combobox_task_id")
    val sliderInterval: Slider by fxid("slider_interval")

    val buttonOK: Button by fxid("btn_ok")
    val buttonCancel: Button by fxid("btn_cancel")

    private var success = false
    companion object {
        fun showDialog(): Boolean{
            val dialog = SetupDialog()
            Stage().apply {
                scene = Scene(dialog.root)
            }.showAndWait()
            return dialog.success
        }
    }

    init {
        title = "Ivy-Lee Task Tracker - Setup"
        icon = imageview(Image(Entrypoint::class.java.getResourceAsStream("/images/frog-hq.png")))

        comboBoxTaskID.editor.alignment = Pos.CENTER
        comboBoxTaskID.editor.font = Font.font("System", 14.0)

        Locale.getAvailableLocales().sortedBy { it.displayLanguage }.forEach {
            if(!languages.items.contains(it.displayLanguage))
                languages.items.add( it.displayLanguage )
        }
        languages.value = Locale.getDefault().displayLanguage
        language.text = Locale.getDefault().language

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
            runAsync { runCatching { ConnectionProvider.connect() }.getOrThrow() }.apply {
                onSucceeded = EventHandler {
                    progressGDrive.progress = 1.0

                    // Add task Ids to combo box
                    comboBoxTaskID.items.addAll( RemoteFilesHandler(ConnectionProvider.connect()).getTaskIds().keys )
                }
            }
        }
        languages.onAction = EventHandler {
            language.text =  Locale.getAvailableLocales().firstOrNull {  it.displayLanguage == languages.value  }?.language?:"N/A"
        }

        buttonCancel.onAction = EventHandler {
            close()
        }
        buttonOK.onAction = EventHandler {
            Entrypoint.CONFIG_FILE.outputStream().writer().use {
                it.write(Configuration(comboBoxTaskID.value, sliderInterval.value.toLong(), ChronoUnit.MONTHS, Locale.forLanguageTag(language.text)).toJsonString())
            }
            success = Entrypoint.CONFIG_FILE.exists()
            close()
        }

    }

}