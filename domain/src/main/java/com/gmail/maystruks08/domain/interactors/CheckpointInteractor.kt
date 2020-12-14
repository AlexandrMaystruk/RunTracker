package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl

interface CheckpointInteractor {

    suspend fun getCheckpoints(distanceId: Long): TaskResult<Exception, List<CheckpointImpl>>

    suspend fun getCurrentSelectedCheckpointId(distanceId: Long): TaskResult<Exception, CheckpointImpl>

    suspend fun saveCurrentSelectedCheckpointId(checkpoint: CheckpointImpl): TaskResult<Exception, Unit>

}