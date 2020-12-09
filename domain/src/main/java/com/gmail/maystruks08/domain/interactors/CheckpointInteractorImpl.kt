package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import javax.inject.Inject

class CheckpointInteractorImpl @Inject constructor(): CheckpointInteractor {

    override suspend fun getCheckpoints(distanceId: Long): TaskResult<Exception, List<Checkpoint>> {
        TODO("Not yet implemented")
    }
}