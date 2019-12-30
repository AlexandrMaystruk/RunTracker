package com.gmail.maystruks08.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class RunnerTable(
    @PrimaryKey
    val id: String,
    val name: String,
    val surname: String,
    val age: Int
)




