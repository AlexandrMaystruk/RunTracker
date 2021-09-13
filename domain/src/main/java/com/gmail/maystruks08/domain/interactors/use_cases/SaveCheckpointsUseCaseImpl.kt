package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

class SaveCheckpointsUseCaseImpl @Inject constructor(
    private val repository: CheckpointsRepository
) : SaveCheckpointsUseCase {

    override suspend fun invoke(
        distanceId: String,
        editedCheckpoints: List<Checkpoint>
    ): TaskResult<Exception, Unit> {
        return TaskResult.build {
            repository.saveEditedCheckpoints(distanceId, editedCheckpoints)
        }
    }
}