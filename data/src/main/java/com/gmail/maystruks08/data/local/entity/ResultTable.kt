package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "result",
    primaryKeys = ["runnerNumber", "checkpointId"],
    foreignKeys = [ForeignKey(
        entity = RunnerTable::class,
        parentColumns = ["number"],
        childColumns = ["runnerNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ResultTable(
    val runnerNumber: Int,
    val checkpointId: Int,
    val time: Date?,
    var hasPrevious: Boolean
)