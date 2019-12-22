package com.gmail.maystruks08.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class CompetitorResultTable(
    @PrimaryKey
    var id: String
)




