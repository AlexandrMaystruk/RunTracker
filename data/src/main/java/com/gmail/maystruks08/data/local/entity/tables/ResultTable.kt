package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "result",
    foreignKeys = [ForeignKey(
        entity = RunnerTable::class,
        parentColumns = ["runnerNumber"],
        childColumns = ["runnerNumber"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ResultTable(
    @PrimaryKey(autoGenerate = true)
    val resultId: Long = 0,
    val checkpointId: String,
    val runnerNumber: String,
    val time: Date,
    var hasPrevious: Boolean
)