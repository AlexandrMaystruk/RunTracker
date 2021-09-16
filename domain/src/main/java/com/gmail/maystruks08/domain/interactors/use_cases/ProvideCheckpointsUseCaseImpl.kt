package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

class ProvideCheckpointsUseCaseImpl @Inject constructor(
    private val repository: CheckpointsRepository
) : ProvideCheckpointsUseCase {

    override suspend fun invoke(
        distanceId: String
    ): List<Checkpoint> {
        return repository.getCheckpoints(distanceId).sortedBy { it.getPosition() }
    }

}