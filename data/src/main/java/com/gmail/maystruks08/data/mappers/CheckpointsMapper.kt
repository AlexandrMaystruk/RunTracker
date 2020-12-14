package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml

fun CheckpointResultIml.toResultTable(runnerNumber: Int): ResultTable {
    return ResultTable(
        resultId = 0,
        checkpointId = 0,
        time = this.getResult(),
        hasPrevious = this.hasPrevious()
    )
}

fun Checkpoint.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        getId().toInt(),
        getDistanceId().toInt(),
        getName(),
    )
}

fun CheckpointPojo.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        this.id.toInt(),
        this.distanceId.toInt(),
        this.name,
    )
}

fun CheckpointTable.toCheckpoint(): CheckpointImpl {
    return CheckpointImpl(
        this.checkpointId.toLong(),
        this.name,
        distanceId.toLong()
    )
}

fun List<CheckpointTable>.toCheckpoints(): List<CheckpointImpl> {
    return ArrayList<CheckpointImpl>().apply {
        this@toCheckpoints.forEach { add(it.toCheckpoint()) }
    }.sortedBy { it.getId() }
}
