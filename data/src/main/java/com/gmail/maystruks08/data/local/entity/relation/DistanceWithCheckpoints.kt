package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable

@DatabaseView
class DistanceWithCheckpoints(
    @Embedded val distance: DistanceTable,
    @Relation(
        parentColumn = "distanceId",
        entityColumn = "distanceId",
        entity = CheckpointTable::class
    )
    val checkpoints: List<CheckpointTable>
)