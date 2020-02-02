package com.gmail.maystruks08.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "checkpoints",
    foreignKeys = [
        ForeignKey(
            entity = RunnerTable::class,
            parentColumns = ["id"],
            childColumns = ["runnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckpointTable(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val runnerId: String,
    val checkpointId: String,
    val date: Date? = null,
    val state: Int
)