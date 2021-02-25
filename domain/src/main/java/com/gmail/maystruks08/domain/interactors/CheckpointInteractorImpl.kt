package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

class CheckpointInteractorImpl @Inject constructor(private val checkpointsRepository: CheckpointsRepository): CheckpointInteractor {

    override suspend fun getCheckpoints(
        raceId: Long,
        distanceId: Long
    ): TaskResult<Exception, List<Checkpoint>> {
        return TaskResult.build {
            checkpointsRepository.getCheckpoints(raceId, distanceId)
        }
    }

    override suspend fun getCurrentSelectedCheckpoint(
        raceId: Long,
        distanceId: Long
    ): TaskResult<Exception, Checkpoint> {
        return TaskResult.build {
            checkpointsRepository.getCurrentCheckpoint(raceId, distanceId) ?: throw CheckpointNotFoundException()
        }
    }

    override suspend fun saveCurrentSelectedCheckpointId(
        raceId: Long,
        distanceId: Long,
        checkpointId: Long
    ): TaskResult<Exception, Unit> {
        return TaskResult.build {
            checkpointsRepository.saveCurrentSelectedCheckpointId(raceId, distanceId, checkpointId)
        }
    }
}