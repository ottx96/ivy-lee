package com.github.ottx96.ivy.gdrive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.github.ottx96.ivy.config.Configuration
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class RemoteFilesHandler(private val service: Drive) {

    fun cleanupFilesOlderThan(time: Long, chronoUnit: ChronoUnit) {
        println("cleaning outdated gdrive files..")
        val olderThan = LocalDateTime.now().minus(time + 1, chronoUnit)
        service.files().list().apply {
            spaces = "appDataFolder"
            orderBy = "modifiedTime"
            pageSize = 1000
            fields = "files(id, name, modifiedTime)"
        }.execute().files.forEach {
            val modifDate = LocalDateTime.parse(it.modifiedTime.toStringRfc3339(), DateTimeFormatter.ofPattern("""yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"""))
            if(olderThan > modifDate){
                println("deleting file from gdrive: $it")
                service.files().delete(it.id).execute()
            }
        }
    }

    fun saveTasks(fileLocation: File): com.google.api.services.drive.model.File{
        val metadata = com.google.api.services.drive.model.File().apply {
            name = Configuration.instance.taskId
            parents = listOf("appDataFolder")
        }
        val content = FileContent("application/octet-stream", fileLocation)

        return service.files().create(metadata, content).execute()
    }

    @Throws(FileNotFoundException::class)
    fun readTasks(fileLocation: File) {
        service.files().list().apply {
            spaces = "appDataFolder"
            q = "name = '${Configuration.instance.taskId}'"
            orderBy = "modifiedTime desc"
            pageSize = 1
            fields = "files(id, name)"
        }.execute().files.firstOrNull()?.apply {
            println("downloading file $name ($id)")
            service.files().get(id).executeMediaAndDownloadTo(fileLocation.outputStream())
        }?:throw FileNotFoundException("No task files stored at gdrive!")
    }

    fun readAllTasks(){
        TODO()
    }

    fun getTaskIds(): Map<String, com.google.api.services.drive.model.File>{
        val ids = mutableMapOf<String, com.google.api.services.drive.model.File>()
        service.files().list().apply {
            spaces = "appDataFolder"
            orderBy = "modifiedTime desc"
            pageSize = 1000
            fields = "files(id, name)"
        }.execute().files.forEach {f ->
            ids[f.name] = f
        }
        return ids
    }

}