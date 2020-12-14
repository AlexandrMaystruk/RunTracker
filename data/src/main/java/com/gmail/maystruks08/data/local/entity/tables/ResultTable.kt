package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "result")
data class ResultTable(
    @PrimaryKey
    val resultId: Long,
    val checkpointId: Int,
    val time: Date?,
    var hasPrevious: Boolean
)