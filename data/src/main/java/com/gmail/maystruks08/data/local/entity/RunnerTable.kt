package com.gmail.maystruks08.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "runners",
    indices = [Index(value = arrayOf("id"), unique = true)]
)
data class RunnerTable(
    @PrimaryKey
    val id: String,
    val number: Int,
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




