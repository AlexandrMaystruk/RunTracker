package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DistanceInteractorImpl @Inject constructor(private val distanceRepository: DistanceRepository) :
    DistanceInteractor {

    override suspend fun observeDistanceData(raceId: Long): Flow<Change<Distance>> {
        return distanceRepository.observeDistanceData(raceId)
    }

    override suspend fun getDistances(raceId: Long): TaskResult<Exception, List<Distance>> {
        return TaskResult.build {
            distanceRepository.getDistanceList(raceId)
        }
    }
}