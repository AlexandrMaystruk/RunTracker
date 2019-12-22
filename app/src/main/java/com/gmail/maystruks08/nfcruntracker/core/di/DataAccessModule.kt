//package com.gmail.maystruks08.nfcruntracker.core.di
//
//import android.content.Context
//import androidx.room.Room
//import dagger.Module
//import dagger.Provides
//import com.gmail.maystruks08.hikingfood.core.executors.BaseExecutor
//import com.gmail.maystruks08.data.room.AppDatabase
//import maystruks08.gmail.com.data.room.dao.MenuDAO
//import com.gmail.maystruks08.domain.executor.ThreadExecutor
//
//
//import javax.inject.Singleton
//
//@Module
//class DataAccessModule {
//
//    @Provides
//    @Singleton
//    fun appDatabase(context: Context): AppDatabase =
//        Room.databaseBuilder(context, AppDatabase::class.java, "sc_db").build()
//
//    @Provides
//    @Singleton
//    fun userDao(appDatabase: AppDatabase): MenuDAO = appDatabase.userDao()
//
//    @Provides
//    @Singleton
//    fun executor(): ThreadExecutor = BaseExecutor()
//
//
//}