package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DistanceInteractorImpl @Inject constructor(
    private val distanceRepository: DistanceRepository
) : DistanceInteractor {

    override suspend fun observeDistanceDataFlow(raceId: String) {
        distanceRepository.observeDistanceDataFlow(raceId)
    }

    override suspend fun getDistancesFlow(raceId: String): Flow<List<Distance>> {
        return distanceRepository.getDistanceListFlow(raceId)
    }
}