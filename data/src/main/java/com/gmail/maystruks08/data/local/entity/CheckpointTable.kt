package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "checkpoints")
data class CheckpointTable(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: Int,
    val startWorkingTime: Date? = null,
    val finishWorkingTime: Date? = null
)