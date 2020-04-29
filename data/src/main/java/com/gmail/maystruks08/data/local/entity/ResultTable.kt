package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(
    tableName = "result",
    primaryKeys = ["runnerId", "checkpointId"],
    foreignKeys = [
        ForeignKey(
            entity = RunnerTable::class,
            parentColumns = ["id"],
            childColumns = ["runnerId"],
            onDelete = ForeignKey.CASCADE
        )]
)
data class ResultTable(
    val runnerId: String,
    val checkpointId: Int,
    val time: Date? = null
)