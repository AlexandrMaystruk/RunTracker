package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "runners", indices = [Index(value = arrayOf("runnerNumber"), unique = true)])
data class RunnerTable(
    @PrimaryKey
    val runnerNumber: Long,
    val cardId: String,
    val fullName: String,
    val shortName: String,
    val phone: String,
    val sex: Int,
    val city: String,
    val dateOfBirthday: Date,
    val distanceIds: String, //json
    val raceIds: String, //json
    val teamName: String?,
    val totalResult: Date?,
    val isOffTrack: Boolean,
    val needToSync: Boolean = true
)




