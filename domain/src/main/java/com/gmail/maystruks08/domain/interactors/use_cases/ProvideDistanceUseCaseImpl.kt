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

    override suspend fun invoke(raceId: String, distanceId: String): Flow<Distance> {
        return distanceRepository
            .getDistanceFlow(raceId, distanceId)
            .onEach {
                withContext(Dispatchers.Default) {
                    launch { calculateDistanceStatisticUseCase.invoke(it.id) }
                }
            }
            .flowOn(Dispatchers.IO)
    }
}