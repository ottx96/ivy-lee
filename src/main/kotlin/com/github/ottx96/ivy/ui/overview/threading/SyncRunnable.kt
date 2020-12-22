package com.github.ottx96.ivy.ui.overview.threading

import com.github.ottx96.ivy.config.Configuration
import com.github.ottx96.ivy.data.IvyLeeTask
import com.github.ottx96.ivy.gdrive.RemoteFilesHandler
import com.github.ottx96.ivy.ui.overview.IvyLee
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.cbor.Cbor
import java.io.File

@ExperimentalSerializationApi
class SyncRunnable(private val remoteFilesHandler: RemoteFilesHandler): Runnable {

    private var cleanCounter = 0

    companion object {
        private const val SYNC_INTERVAL = 30 * 60 * 1000L      // 30 minutes
        private const val CLEANUP_INTERVAL = 180 * 60 * 1000L // 180 minutes
    }

    init {
        assert(SYNC_INTERVAL <= CLEANUP_INTERVAL) {"SYNC_INTERVAL has to be smaller than CLEANUP_INTERVAL!"}
    }

    override fun run() {
        while(true) {
            synchronizeTasks()
        }
    }

    private fun synchronizeTasks() {
        try {
            Thread.sleep(SYNC_INTERVAL)
            if (shouldCleanup())
                remoteFilesHandler.cleanupFilesOlderThan(Configuration.instance.cleanInterval, Configuration.instance.timeUnit)
            syncTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun syncTasks() {
        println("Synchronizing tasks to Google Drive..")
        File("tasks-auto-sync.db").apply {
            writeBytes(Cbor.encodeToByteArray(
                    ListSerializer(IvyLeeTask.serializer()),
                    IvyLee.tasks.values.toList()))
            remoteFilesHandler.saveTasks(this)
        }
    }

    private fun shouldCleanup(): Boolean {
        if(cleanCounter * SYNC_INTERVAL >= CLEANUP_INTERVAL){
            cleanCounter = 0
            return true
        }
        return false
    }

}