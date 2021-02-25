package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_settings",
    primaryKeys = ["userId", "raceId", "distanceId"]
)
data class UserSettingsTable(
    val userId: String,
    val raceId: Long,
    val distanceId: Long,

    val currentCheckpointId: Long?,
)