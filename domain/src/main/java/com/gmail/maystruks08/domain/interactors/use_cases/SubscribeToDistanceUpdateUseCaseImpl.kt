package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.repository.DistanceRepository
import javax.inject.Inject

class SubscribeToDistanceUpdateUseCaseImpl @Inject constructor(
    private val distanceRepository: DistanceRepository,
) : SubscribeToDistanceUpdateUseCase {

    override suspend fun invoke() {
        val currentRaceId = distanceRepository.getLastSelectedRace().first
        distanceRepository.observeDistanceDataFlow(currentRaceId)
    }
}

