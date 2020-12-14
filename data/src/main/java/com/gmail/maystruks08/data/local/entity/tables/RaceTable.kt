package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "race_table", indices = [Index(value = arrayOf("id"))])
data class RaceTable(
    @PrimaryKey
    val id: Long,
    val date: Long,
    val authorId: String,
    val name: String
)