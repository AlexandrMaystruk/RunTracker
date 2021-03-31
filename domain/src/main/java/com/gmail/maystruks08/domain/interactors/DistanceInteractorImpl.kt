package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DistanceInteractorImpl @Inject constructor(
    private val distanceRepository: DistanceRepository,
    private val calculateDistanceStatisticUseCase: CalculateDistanceStatisticUseCase
) : DistanceInteractor {

    override suspend fun observeDistanceDataFlow(raceId: String) {
        distanceRepository.observeDistanceDataFlow(raceId)
    }

    override suspend fun getDistancesFlow(raceId: String): Flow<List<Distance>> {
        return distanceRepository.getDistanceListFlow(raceId).onEach {
            withContext(Dispatchers.Default) {
                it.forEach { launch { calculateDistanceStatisticUseCase.invoke(raceId, it.id) } }
            }
        }
    }
}