package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import androidx.room.Room
import com.gmail.maystruks08.data.local.AppDatabase
import com.gmail.maystruks08.data.local.RunnerDAO
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.remote.FirestoreApiImpl
import com.gmail.maystruks08.data.remote.googledrive.DriveCredentialsProvider
import com.gmail.maystruks08.data.remote.googledrive.GoogleDriveApi
import com.gmail.maystruks08.data.repository.SettingsRepositoryImpl
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @JvmStatic
    @Provides
    @Singleton
    fun appDatabase(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "sc_db").build()

    @JvmStatic
    @Provides
    @Singleton
    fun userDao(appDatabase: AppDatabase): RunnerDAO = appDatabase.runnerDao()

    @JvmStatic
    @Provides
    @Singleton
    fun firestoreApi(): FirestoreApi = FirestoreApiImpl(FirebaseFirestore.getInstance())

    @JvmStatic
    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @JvmStatic
    @Provides
    @Singleton
    fun credentialsProvider(context: Context): DriveCredentialsProvider = DriveCredentialsProvider(context)

    @JvmStatic
    @Provides
    @Singleton
    fun googleDriveApi(context: Context, provider: DriveCredentialsProvider): GoogleDriveApi = GoogleDriveApi(context, provider)

    @JvmStatic
    @Provides
    @Singleton
    fun gson(): Gson = Gson()
}