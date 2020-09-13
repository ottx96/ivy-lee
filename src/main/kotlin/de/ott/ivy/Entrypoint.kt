package de.ott.ivy

import de.ott.ivy.ui.IvyLee
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.enum.TaskStatus
import de.ott.ivy.ui.dialog.SetupDialog
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import tornadofx.App
import java.io.File
import kotlin.system.exitProcess

@ExperimentalSerializationApi
class Entrypoint: App(IvyLee::class){

    companion object{
        val CONFIG_FOLDER = File("config/")
    }

    override fun start(stage: Stage) {
        // check, if initial run
        if(!CONFIG_FOLDER.exists())
            // run setup!
            if(!SetupDialog.showDialog()){
                // Setup was cancelled!
                Platform.exit()
                exitProcess(0)
            }

        stage.icons.add(Image(Entrypoint::class.java.getResourceAsStream("/de/ott/ivy/images/frog-hq.png")))
        stage.onCloseRequest = EventHandler {
            val tasksFile = File("tasks.db")

            //Speichern auf nFile
            println("Exit..")
            IvyLee.tasks.values.filter { it.status == TaskStatus.IN_WORK }.forEach {
                it.status = TaskStatus.UNDONE
            }
            println("dumping tasks..")
            tasksFile.writeBytes(Cbor.encodeToByteArray(ListSerializer(IvyLeeTask.serializer()), IvyLee.tasks.values.toList()))
            println("uploading tasks to gdrive..")
            IvyLee.gdrive.saveTasks(tasksFile)
        }

        super.start(stage)
    }
}