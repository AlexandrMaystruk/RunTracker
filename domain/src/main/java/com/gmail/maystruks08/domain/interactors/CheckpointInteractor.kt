package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointInteractor {

    suspend fun getCheckpoints(
        raceId: Long,
        distanceId: Long
    ): TaskResult<Exception, List<Checkpoint>>

    suspend fun getCurrentSelectedCheckpoint(
        raceId: Long,
        distanceId: Long
    ): TaskResult<Exception, Checkpoint>

    suspend fun saveCurrentSelectedCheckpointId(
        raceId: Long,
        distanceId: Long,
        checkpointId: Long
    ): TaskResult<Exception, Unit>

}