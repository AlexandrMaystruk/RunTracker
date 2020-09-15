package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import java.util.*

@Entity(
    primaryKeys = ["cardId", "number"], tableName = "runners",
    indices = [Index(value = arrayOf("number"), unique = true)]
)
data class RunnerTable(
    val number: Int,
    val cardId: String,
    val fullName: String,
    val shortName: String,
    val phone: String,
    val sex: Int,
    val city: String,
    val dateOfBirthday: Date,
    val type: Int,
    val teamName: String?,
    val totalResult: Date?,
    val isOffTrack: Boolean,
    val needToSync: Boolean = true
)




