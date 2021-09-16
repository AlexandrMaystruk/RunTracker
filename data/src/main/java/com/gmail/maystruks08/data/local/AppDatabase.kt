package com.gmail.maystruks08.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gmail.maystruks08.data.local.dao.*
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerResultCrossRef
import com.gmail.maystruks08.data.local.entity.tables.*

@Database(
    entities = [
        RaceTable::class,
        DistanceTable::class,
        RunnerTable::class,
        CheckpointTable::class,
        ResultTable::class,
        DistanceRunnerCrossRef::class,
        RunnerResultCrossRef::class,
        UserSettingsTable::class,
        TeamNameTable::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun raceDao(): RaceDAO

    abstract fun distanceDao(): DistanceDAO

    abstract fun runnerDao(): RunnerDao

    abstract fun checkpointDao(): CheckpointDAO

    abstract fun userSettingsDAO(): UserSettingsDAO

}