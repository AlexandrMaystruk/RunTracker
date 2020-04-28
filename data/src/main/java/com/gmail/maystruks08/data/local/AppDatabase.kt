package com.gmail.maystruks08.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.local.entity.CheckpointTable
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable

@Database(entities = [RunnerTable::class, CheckpointTable::class, ResultTable::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun runnerDao(): RunnerDao

    abstract fun checkpointDao(): CheckpointDAO

}