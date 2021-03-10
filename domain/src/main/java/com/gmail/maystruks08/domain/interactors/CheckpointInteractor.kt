package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointInteractor {

    suspend fun getCheckpoints(
        raceId: String,
        distanceId: String
    ): TaskResult<Exception, List<Checkpoint>>

    suspend fun getCurrentSelectedCheckpoint(
        raceId: String,
        distanceId: String
    ): TaskResult<Exception, Checkpoint>

    suspend fun saveCurrentSelectedCheckpointId(
        raceId: String,
        distanceId: String,
        checkpointId: Long
    ): TaskResult<Exception, Unit>

}