package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import androidx.room.Room
import com.gmail.maystruks08.data.local.AppDatabase
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.remote.FirestoreApiImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
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
    fun userDao(appDatabase: AppDatabase): RunnerDao = appDatabase.runnerDao()

    @JvmStatic
    @Provides
    @Singleton
    fun provideCheckpointDao (appDatabase: AppDatabase): CheckpointDAO = appDatabase.checkpointDao()

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
    fun gson(): Gson = Gson()
}