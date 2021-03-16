package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.RaceRepository
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class RaceInteractorImpl @Inject constructor(
    private val repository: RaceRepository
) : RaceInteractor {

    override suspend fun subscribeToUpdates() {
        return repository.subscribeToUpdates()
    }

    override suspend fun getRaceList(): Flow<List<Race>> {
        return repository.getRaceList()
    }

    override suspend fun getRaceList(query: String): TaskResult<Exception, List<Race>> {
        return TaskResult.build {
            return@build repository.getRaceList(query)
        }
    }

    override suspend fun saveLastSelectedRace(raceId: String, raceName: String): TaskResult<Exception, Unit> {
        return TaskResult.build {
            return@build repository.saveLastSelectedRaceId(raceId, raceName)
        }
    }

    override suspend fun getLastSelectedRace(): TaskResult<Exception, Pair<String, String>> {
        return TaskResult.build {
            return@build repository.getLastSelectedRace()
        }
    }
}