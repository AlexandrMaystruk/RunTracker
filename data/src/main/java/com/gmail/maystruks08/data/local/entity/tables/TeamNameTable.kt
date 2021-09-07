package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "teams",
    foreignKeys = [ForeignKey(
        entity = RunnerTable::class,
        parentColumns = ["runnerNumber"],
        childColumns = ["runnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TeamNameTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val distanceId: String,
    val runnerId: String,
    var name: String
)