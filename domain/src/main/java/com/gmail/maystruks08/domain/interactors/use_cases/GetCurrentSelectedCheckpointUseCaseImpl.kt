package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

class GetCurrentSelectedCheckpointUseCaseImpl @Inject constructor(
    private val provideCurrentRaceIdUseCase: ProvideCurrentRaceIdUseCase,
    private val checkpointsRepository: CheckpointsRepository
) : GetCurrentSelectedCheckpointUseCase {

    override suspend fun invoke(distanceId: String): Checkpoint {
        val raceId = provideCurrentRaceIdUseCase.invoke()
        return checkpointsRepository.getCurrentCheckpoint(raceId, distanceId) ?: throw CheckpointNotFoundException()
    }
}