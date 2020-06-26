package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.CheckpointTable
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointResult
import com.gmail.maystruks08.domain.entities.CheckpointType

fun CheckpointResult.toResultTable(runnerId: String): ResultTable {
    return ResultTable(
        runnerId = runnerId,
        checkpointId = id,
        time = this.date,
        hasPrevious = this.hasPrevious
    )
}

fun Checkpoint.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        this.id,
        this.type.ordinal,
        this.name,
        null, //TODO implement
        null //TODO implement
    )
}

fun CheckpointPojo.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        this.id,
        this.type,
        this.name,
        null, //TODO implement
        null //TODO implement
    )
}

fun CheckpointTable.toCheckpoint(): Checkpoint{
    return Checkpoint(
        this.checkpointId,
        this.name,
        CheckpointType.fromOrdinal(this.checkpointType)
    )
}
