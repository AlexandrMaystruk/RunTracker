package com.gmail.maystruks08.data.local.entity.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "race_table", indices = [Index(value = arrayOf("id"))])
data class RaceTable(
    @PrimaryKey
    val id: String,
    val name: String,
    val dateCreation: Long,
    val authorId: String,
    val registrationIsOpen: Boolean,
    val adminListIds: String, //json
    val distanceListIds: String //json
)