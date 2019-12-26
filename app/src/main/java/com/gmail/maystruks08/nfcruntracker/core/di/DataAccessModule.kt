package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import androidx.room.Room
import com.gmail.maystruks08.data.local.AppDatabase
import com.gmail.maystruks08.data.local.CompetitorDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataAccessModule {

    @Provides
    @Singleton
    fun appDatabase(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "sc_db").build()

    @Provides
    @Singleton
    fun userDao(appDatabase: AppDatabase): CompetitorDAO = appDatabase.competitorDao()

}