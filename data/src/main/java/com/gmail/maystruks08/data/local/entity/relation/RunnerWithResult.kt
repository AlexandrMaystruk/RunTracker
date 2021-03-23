package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable

@DatabaseView
data class RunnerWithResult(
    @Embedded val runnerTable: RunnerTable,
    @Relation(
        parentColumn = "runnerNumber",
        entityColumn = "runnerNumber",
        associateBy = Junction(RunnerResultCrossRef::class)
    )
    val results: List<ResultTable>
)