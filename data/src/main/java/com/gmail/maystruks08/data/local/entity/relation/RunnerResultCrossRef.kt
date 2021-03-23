package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.Entity

@Entity(
    tableName = "runner_result_cross_ref",
    primaryKeys = ["runnerNumber", "resultId"]
)
data class RunnerResultCrossRef(val runnerNumber: Long, val resultId: String)