package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.CheckpointTable
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointResult

fun Checkpoint.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        id = this.id,
        name = this.name,
        type = 0 //TODO
    )
}

fun CheckpointResult.toResultTable(runnerId: String): ResultTable {
    return ResultTable(
        runnerId = runnerId,
        checkpointId = id,
        time = this.date
    )
}
