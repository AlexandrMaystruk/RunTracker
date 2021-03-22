package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "checkpoints",
    primaryKeys = ["checkpointId", "distanceId"],
    indices = [Index(value = arrayOf("checkpointId"))]
)
data class CheckpointTable(
    val checkpointId: String,
    val distanceId: String,
    val name: String
)