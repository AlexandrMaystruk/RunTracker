package com.gmail.maystruks08.data.remote.googledrive

import android.content.Context
import com.gmail.maystruks08.data.remote.googledrive.DriveCredentialsProvider.Companion.APPLICATION_NAME
import com.gmail.maystruks08.data.remote.googledrive.DriveCredentialsProvider.Companion.JSON_FACTORY
import com.gmail.maystruks08.data.remote.googledrive.DriveCredentialsProvider.Companion.httpTransport
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import java.security.GeneralSecurityException
import javax.inject.Inject
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class GoogleDriveApi @Inject constructor(context: Context, private val credentialsProvider: DriveCredentialsProvider) {

    init {
        val parent = context.getDir("cred", Context.MODE_PRIVATE)
        if (!parent.exists()) {
            throw IllegalStateException("Unable to create directory")
        }
        context.assets.open("client_secret.json").use { inputStream ->
            val objectFile = File(parent, "client_secret.json")
            FileOutputStream(objectFile).use { fos ->
                ObjectOutputStream(fos).use {
                    it.write(inputStream.read(ByteArray(inputStream.available())))
                }
            }
            inputStream
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    suspend fun getFile() {
        withContext(Dispatchers.IO) {

            val service = Drive.Builder(httpTransport, JSON_FACTORY, credentialsProvider.getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build()

            val result: FileList = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute()

            val files: List<File> = result.files
            if (files.isEmpty()) {
                println("Drive -> No files found.")
            } else {
                println("Drive -> Files:")
                files.forEach { System.out.printf("%s (%s)\n", it.name, it.id) }
            }
        }
    }
}