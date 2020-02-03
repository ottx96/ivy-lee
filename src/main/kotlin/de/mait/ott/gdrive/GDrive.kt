package de.mait.ott.gdrive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.File
import java.io.IOException
import java.io.InputStreamReader


class GDrive(val credentialFile: File) {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            GDrive(File("src/main/resources/client_secret_15351468828-v484c0nlu2s6q2km4igjco4n01or6klo.apps.googleusercontent.com.json")).test()
        }
    }

    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? { // Load client secrets.

        val clientSecrets =
            GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), InputStreamReader(credentialFile.inputStream()))
        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder( HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), clientSecrets, listOf(DriveScopes.DRIVE_METADATA_READONLY) ).apply {
            setDataStoreFactory(FileDataStoreFactory(File("tokens")))
//            setAccessType("offline")
        }.build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()

        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun test(){
        // Build a new authorized API client service.
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service = Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT)).apply {
            setApplicationName("")
        }.build()

//
        var pageToken: String? = null
        do {
            service.files().list().setPrettyPrint(true).setPageToken(pageToken).setSpaces("drive").execute().apply {
                files.forEach(::println)
                pageToken = nextPageToken
            }
        }while (pageToken != null)

//        val files: FileList = service.files().list()
//            .setSpaces("appDataFolder")
//            .setFields("nextPageToken, files(id, name)")
//            .setPageSize(10)
//            .execute()
//        for (file in files.files) {
//            System.out.printf(
//                "Found file: %s (%s)\n",
//                file.name, file.id
//            )
//        }
//
//
//        val fileMetadata = com.google.api.services.drive.model.File()
//        fileMetadata.setName("test.txt")
//        fileMetadata.setParents(listOf("appDataFolder"))
//        val filePath = File("src/main/resources/test.txt")
//        val mediaContent = FileContent("application/json", filePath)
//        val file = service.files().create(fileMetadata, mediaContent)
//            .setFields("id")
//            .execute()
//        System.out.println("File ID: " + file.getId())
    }

}