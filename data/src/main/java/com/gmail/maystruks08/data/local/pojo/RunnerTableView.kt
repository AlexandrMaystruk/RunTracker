package com.gmail.maystruks08.data.local.pojo

import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable

data class RunnerTableView(

    val runnerTable: RunnerTable,
    val results: List<ResultTable>

)




