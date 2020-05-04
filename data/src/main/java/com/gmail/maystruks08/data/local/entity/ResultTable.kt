package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "result",
    primaryKeys = ["runnerId", "checkpointId"],
    foreignKeys = [ForeignKey(
        entity = RunnerTable::class,
        parentColumns = ["id"],
        childColumns = ["runnerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["runnerId", "checkpointId"])]
)
data class ResultTable(
    val runnerId: String,
    val checkpointId: Int,
    val time: Date?,
    var hasPrevious: Boolean
)