package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

class CheckpointInteractorImpl @Inject constructor(private val checkpointsRepository: CheckpointsRepository): CheckpointInteractor {

    override suspend fun getCheckpoints(distanceId: Long): TaskResult<Exception, List<CheckpointImpl>> {
        return TaskResult.build {
            checkpointsRepository.getCheckpoints(distanceId)
        }
    }

    override suspend fun getCurrentSelectedCheckpointId(distanceId: Long): TaskResult<Exception, CheckpointImpl> {
        return TaskResult.build {
            checkpointsRepository.getCurrentCheckpoint(distanceId)
        }
    }

    override suspend fun saveCurrentSelectedCheckpointId(checkpoint: CheckpointImpl): TaskResult<Exception, Unit> {
        return TaskResult.build {
            checkpointsRepository.saveCurrentSelectedCheckpoint(checkpoint)
        }
    }
}