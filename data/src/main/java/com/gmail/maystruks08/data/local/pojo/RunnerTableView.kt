package com.gmail.maystruks08.data.local.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable

data class RunnerTableView(

    @Embedded val runnerTable: RunnerTable,

    @Relation(parentColumn = "id", entityColumn = "runnerId", entity = ResultTable::class)
    val checkpointsResultTable: List<ResultTableView>

)




