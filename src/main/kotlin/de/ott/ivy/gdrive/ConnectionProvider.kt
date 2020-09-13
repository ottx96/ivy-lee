package de.ott.ivy.gdrive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.security.GeneralSecurityException


object ConnectionProvider {
    private const val APPLICATION_NAME = "IvyLeeTracker (Desktop)"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES = listOf(DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_FILE)
    private const val CREDENTIALS_FILE_PATH = "/de/ott/ivy/gdrive/credentials.json"

    private var receiver: LocalServerReceiver? = null

    /**
     * Creates an authorized Credential object.
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private fun getCredentials(httpTransport: NetHttpTransport): Credential {
        // Load client secrets.
        val inputRes = ConnectionProvider::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputRes))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()

        return AuthorizationCodeInstalledApp(flow, createReceiver()).authorize("user")
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    fun connect(): Drive {
        // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        return Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun createReceiver(): LocalServerReceiver {
        receiver?.stop()
        receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return receiver!!
    }
}