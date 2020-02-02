package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.CheckpointTable
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointState

fun CheckpointTable.toCheckpoint(): Checkpoint {
    return Checkpoint(
        id = this.checkpointId,
        state = CheckpointState.fromId(this.state),
        date = this.date
    )
}

fun Checkpoint.toCheckpointTable(runnerId: String): CheckpointTable {
    return CheckpointTable(
        runnerId = runnerId,
        checkpointId = this.id,
        state = this.state.id,
        date = this.date
    )
}