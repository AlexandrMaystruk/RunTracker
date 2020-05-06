package com.gmail.maystruks08.data.local.pojo

import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable

data class RunnerTableView(

    val runnerTable: RunnerTable,
    val results: List<ResultTable>

)




