package de.ott.ivy

import de.ott.ivy.ui.overview.IvyLee
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.data.enums.TaskStatus
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

/**
 * **Entry point of the application.**
 * Uploads tasks on closing.
 *
 * Runs [IvyLee].
 * @see [IvyLee]
 *
 * UML: use case diagram
 * @startuml
 * frame "uc main application" {
 * left to right direction
 * actor gdrive
 * actor user
 *  rectangle application {
 *      gdrive -- (setup)
 *      (edit task) .> (create task): <<include>>
 *      gdrive --> (synchronize)
 *      (synchronize) ..> (create task): <<extend>>
 *      (synchronize) ..> (edit task): <<extend>>
 *      user -- (setup)
 *      user --> (create task)
 *      user --> (edit task)
 *  }
 * }
 * @enduml
 *
 * UML: activity diagram
 * @startuml
 * |User|
 * start
 * :right click task cell;
 * |Application|
 * if (task present?) then (true)
 *   :load task instance (task data);
 * else (no)
 *   :initialize empty task instance;
 * endif
 * :initialize task dialog;
 * :load task data to GUI;
 * |User|
 * :input (modify) task data;
 * if(click button) then (cancel)
 *   |User|
 *   end
 * else (ok)
 *   |Application|
 *   :save task instance;
 *   fork
 *     |Application|
 *     :close dialog;
 *     :refresh GUI;
 *   fork again
 *     |Google Drive|
 *     :synchronize tasks to cloud;
 *     detach
 *   |Application|
 *   endfork
 *   stop
 * endif
 * |Google Drive|
 * @enduml
 */
@ExperimentalSerializationApi
class Entrypoint: App(IvyLee::class){

    companion object{
        val CONFIG_FILE = File("config/config.json")
    }

    override fun start(stage: Stage) {
        // check, if initial run
        if(!CONFIG_FILE.exists())
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