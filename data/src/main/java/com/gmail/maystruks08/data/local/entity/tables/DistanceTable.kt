package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity

@Entity(
    tableName = "distances",
    primaryKeys = ["distanceId", "raceId"]
)
data class DistanceTable(
    val distanceId: String,
    val raceId: String,
    val authorId: String,
    val name: String,
    val type: String,
    val dateOfStart: Long?,
    val runnerCountInProgress: Int = 0,
    val runnerCountOffTrack: Int = 0,
    val finisherCount: Int = 0
)