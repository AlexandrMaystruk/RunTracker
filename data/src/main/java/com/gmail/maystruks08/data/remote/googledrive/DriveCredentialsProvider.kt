package com.gmail.maystruks08.data.remote.googledrive

import android.content.Context
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import javax.inject.Inject


class DriveCredentialsProvider @Inject constructor(private val context: Context) {

    private var credential: Credential? = null

    companion object {

        val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
        const val APPLICATION_NAME = "Run Tracker"
        const val CLIENT_SECRET_FILE_NAME = "client_secret.json"
        val SCOPES = listOf(DriveScopes.DRIVE)
        val httpTransport = NetHttpTransport()
    }

    @Throws(IOException::class, NoClassDefFoundError::class)
     suspend fun  getCredentials(): Credential {
        return if(credential != null){
            credential!!
        } else {
            withContext(Dispatchers.IO) { getCredentialsFromGoogle() }
            credential!!
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class, NoClassDefFoundError::class)
   private suspend fun getCredentialsFromGoogle() {
        withContext(Dispatchers.IO) {
            val inputStream: InputStream = context.assets.open(CLIENT_SECRET_FILE_NAME)
            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
            inputStream.close()

            val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(context.getDir("cred", Context.MODE_PRIVATE)))
                .setAccessType("online")
                .build()

            credential = AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
        }
    }
}