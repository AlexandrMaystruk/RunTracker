package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.RaceRepository
import javax.inject.Inject

class RaceInteractorImpl @Inject constructor(private val repository: RaceRepository): RaceInteractor {

    override suspend fun getRaceList(): TaskResult<Exception, List<Race>> {
        return TaskResult.build {
            return@build repository.getRaceList()
        }
    }

    override suspend fun saveLastSelectedRaceId(raceId: Long): TaskResult<Exception, Unit> {
        return TaskResult.build {
            return@build repository.saveLastSelectedRaceId(raceId)
        }
    }

    override suspend fun getLastSelectedRaceId(): TaskResult<Exception, Long> {
        return TaskResult.build {
            return@build repository.getLastSelectedRaceId()
        }
    }
}