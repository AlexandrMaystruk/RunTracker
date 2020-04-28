package com.gmail.maystruks08.data.local.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.gmail.maystruks08.data.local.entity.CheckpointTable
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable

data class ResultTableView(

    @Embedded val resultTable: ResultTable,

    @Relation(parentColumn = "checkpointId", entityColumn = "id")
    val checkpointTable: CheckpointTable
)




