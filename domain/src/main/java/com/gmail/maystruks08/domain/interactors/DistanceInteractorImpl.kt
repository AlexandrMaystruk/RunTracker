package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.DistanceRepository
import javax.inject.Inject

class DistanceInteractorImpl @Inject constructor(private val distanceRepository: DistanceRepository) :
    DistanceInteractor {

    override suspend fun getDistances(): TaskResult<Exception, List<Distance>> {
        return TaskResult.build {
            distanceRepository.getDistanceList()
        }
    }
}