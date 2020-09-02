package de.ott.ivy.gdrive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime

class ApplicationDataHandler(val service: Drive) {

    fun resetData(){
        TODO()
    }

    fun saveTasks(fileLocation: File): com.google.api.services.drive.model.File{
        val metadata = com.google.api.services.drive.model.File().apply {
            name = "tasks_${LocalDateTime.now()}"
            parents = listOf("appDataFolder")
        }
        val content = FileContent("application/octet-stream", fileLocation)

        return service.files().create(metadata, content)
            .setFields("id")
            .execute()
    }

    @Throws(FileNotFoundException::class)
    fun readTasks(fileLocation: File) {
        service.files().list().apply {
            spaces = "appDataFolder"
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

}