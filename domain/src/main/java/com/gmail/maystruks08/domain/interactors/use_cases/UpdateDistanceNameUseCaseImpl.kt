package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.DistanceRepository
import javax.inject.Inject

class UpdateDistanceNameUseCaseImpl @Inject constructor(
    private val repository: DistanceRepository
) : UpdateDistanceNameUseCase {

    override suspend fun invoke(distanceId: String, newName: String): TaskResult<Exception, Unit> {
        return TaskResult.build {
            repository.updateDistanceName(distanceId, newName)
        }
    }
}