package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable

@DatabaseView
class DistanceWithRunners(
    @Embedded val distance: DistanceTable,
    @Relation(
        parentColumn = "distanceId",
        entityColumn = "runnerNumber",
        associateBy = Junction(DistanceRunnerCrossRef::class)
    )
    val runners: List<RunnerTable>?,

    @Relation(
        parentColumn = "distanceId",
        entityColumn = "checkpointId",
        entity = CheckpointTable::class
    )
    val checkpoints: List<CheckpointTable>
)