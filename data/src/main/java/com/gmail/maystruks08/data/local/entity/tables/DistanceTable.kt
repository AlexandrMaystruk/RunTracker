package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "distances",
    primaryKeys = ["distanceId", "raceId"],
    foreignKeys = [ForeignKey(
        entity = RaceTable::class,
        parentColumns = ["id"],
        childColumns = ["raceId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = arrayOf("distanceId"))]
)
data class DistanceTable(
    val distanceId: Long,
    val raceId: Long,
    val authorId: String,
    val name: String
)