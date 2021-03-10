package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import com.gmail.maystruks08.data.local.entity.tables.RaceTable

@DatabaseView
data class RaceWithDistances(
    @Embedded val raceTable: RaceTable,
    @Relation(
        parentColumn = "id",
        entityColumn = "distanceId",
        entity = DistanceTable::class
    )
    val distancesWithRunners: List<DistanceWithRunners>
)