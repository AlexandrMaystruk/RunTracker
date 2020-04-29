package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.domain.entities.CheckpointResult

fun CheckpointResult.toResultTable(runnerId: String): ResultTable {
    return ResultTable(
        runnerId = runnerId,
        checkpointId = id,
        time = this.date,
        hasPrevious = this.hasPrevious
    )
}
