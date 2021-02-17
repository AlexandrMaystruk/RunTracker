package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.RaceRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RaceInteractorImpl @Inject constructor(private val repository: RaceRepository): RaceInteractor {

    @FlowPreview
    override suspend fun subscribeToUpdates(): Flow<List<Race>> {
        return repository
            .subscribeToUpdates()
            .map { repository.getRaceList() }
    }

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