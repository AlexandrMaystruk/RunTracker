package com.gmail.maystruks08.data.local

import androidx.room.Embedded

import androidx.room.Relation

data class RunnerWithCheckpoints (
    @Embedded val  runnerTable: RunnerTable,
    @Relation(parentColumn = "id", entityColumn = "runnerId")
    val checkpointsTable: List<CheckpointTable>
)




