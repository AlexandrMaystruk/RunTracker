package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.RaceRepository
import javax.inject.Inject

class RaceInteractorImpl @Inject constructor(private val repository: RaceRepository): RaceInteractor {

    override suspend fun getRaceList(): TaskResult<Exception, List<Race>> {
        return TaskResult.build {
            repository.getRaceList()
        }
    }

    override suspend fun saveLastSelectedRaceId(raceId: Long) {
        repository.saveLastSelectedRaceId(raceId)
    }
}