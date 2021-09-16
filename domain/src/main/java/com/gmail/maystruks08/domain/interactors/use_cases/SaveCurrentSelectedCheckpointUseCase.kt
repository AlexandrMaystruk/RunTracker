package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject

interface SaveCurrentSelectedCheckpointUseCase {

    @Throws(CheckpointNotFoundException::class)
    suspend fun invoke(
        distanceId: String,
        checkpointId: String
    )

}


class SaveCurrentSelectedCheckpointUseCaseImpl @Inject constructor(
    private val provideCurrentRaceIdUseCase: ProvideCurrentRaceIdUseCase,
    private val repository: CheckpointsRepository
) : SaveCurrentSelectedCheckpointUseCase {

    @Throws(CheckpointNotFoundException::class)
    override suspend fun invoke(
        distanceId: String,
        checkpointId: String
    ) {
        val currentRaceId = provideCurrentRaceIdUseCase.invoke()
        repository.saveCurrentSelectedCheckpointId(currentRaceId, distanceId, checkpointId)
    }

}