package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import kotlinx.coroutines.flow.Flow

interface RaceInteractor {

    suspend fun subscribeToUpdates(): Flow<List<Race>>

    suspend fun getRaceList(): TaskResult<Exception, List<Race>>

    suspend fun saveLastSelectedRaceId(raceId: Long): TaskResult<Exception, Unit>

    suspend fun getLastSelectedRaceId(): TaskResult<Exception, Long>

}