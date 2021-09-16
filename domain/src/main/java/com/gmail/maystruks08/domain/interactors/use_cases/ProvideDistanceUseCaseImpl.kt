package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProvideDistanceUseCaseImpl @Inject constructor(
    private val distanceRepository: DistanceRepository,
    private val calculateDistanceStatisticUseCase: CalculateDistanceStatisticUseCase
) : ProvideDistanceUseCase {

    override suspend fun invoke(): Flow<List<Distance>> {
        val raceId = distanceRepository.getLastSelectedRace().first
        return distanceRepository.getDistanceListFlow(raceId).onEach {
            withContext(Dispatchers.Default) {
                it.forEach {
                    launch {
                        calculateDistanceStatisticUseCase.invoke(raceId, it.id)
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}