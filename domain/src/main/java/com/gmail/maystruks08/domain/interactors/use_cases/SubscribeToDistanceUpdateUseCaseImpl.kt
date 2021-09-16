package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SubscribeToDistanceUpdateUseCaseImpl @Inject constructor(
    private val distanceRepository: DistanceRepository,
) : SubscribeToDistanceUpdateUseCase {

    override suspend fun invoke(): Flow<Unit> {
        val currentRaceId = distanceRepository.getLastSelectedRace().first
        return distanceRepository
            .observeDistanceDataFlow(currentRaceId)
            .flowOn(Dispatchers.IO)
    }
}

