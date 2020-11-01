package de.ott.ivy.ui.overview.threading

import de.ott.ivy.config.Configuration
import de.ott.ivy.data.IvyLeeTask
import de.ott.ivy.gdrive.RemoteFilesHandler
import de.ott.ivy.ui.overview.IvyLee
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import java.io.File

@ExperimentalSerializationApi
class SyncRunnable(private val gdrive: RemoteFilesHandler): Runnable {
    override fun run() {
        while (true) {
            var ct = 0
            try {
                Thread.sleep(1000 * 60 * 15) // 15 minutes
                if (++ct % 10 == 0) // 150 minutes
                    gdrive.cleanupFilesOlderThan(Configuration.instance.cleanInterval, Configuration.instance.timeUnit)
                // write file
                println("syncing tasks to gdrive..")
                val tasks = File("tasks-auto-sync.db")
                tasks.writeBytes(Cbor.encodeToByteArray(ListSerializer(IvyLeeTask.serializer()), IvyLee.tasks.values.toList()))
                gdrive.saveTasks(tasks)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}