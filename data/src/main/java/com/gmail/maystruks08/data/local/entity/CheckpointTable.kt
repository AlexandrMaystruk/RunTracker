package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import java.util.*

@Entity(
    tableName = "checkpoints",
    primaryKeys = ["checkpointId", "checkpointType"],
    indices = [Index(value = arrayOf("checkpointId"))]
)
data class CheckpointTable(
    val checkpointId: Int,
    val checkpointType: Int,
    val name: String,
    val startWorkingTime: Date? = null,
    val finishWorkingTime: Date? = null
)