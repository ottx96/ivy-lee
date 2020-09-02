package de.ott.ivy.gdrive

import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import java.io.File
import java.io.FileNotFoundException

class ApplicationDataHandler(val service: Drive) {

    fun resetData(){
        TODO()
    }

    fun saveTasks(){
        val fileMetadata: com.google.api.services.drive.model.File = com.google.api.services.drive.model.File()
        val filePath = File("IvyLeeTasks/tasks.2020-09-02_10-40.db")
        fileMetadata.setName(filePath.name)
        fileMetadata.setParents(listOf("appDataFolder"))
        val mediaContent = FileContent("application/octet-stream", filePath)
        val file: com.google.api.services.drive.model.File = service.files().create(fileMetadata, mediaContent)
            .setFields("id")
            .execute()
        println("File ID: " + file.getId())
    }

    @Throws(FileNotFoundException::class)
    fun readTasks(){
        service.files().list().apply {
            spaces = "appDataFolder"
            orderBy = "modifiedTime desc"
            pageSize = 1
            fields = "files(id, name)"
        }.execute().files.firstOrNull()?.apply {
            println("downloading file $name ($id)")
            service.files().get(id).executeMediaAndDownloadTo(File("test.db").outputStream())
        }?:throw FileNotFoundException("No task-files store at gdrive!")
    }

    fun readAllTasks(){
        TODO()
    }

}