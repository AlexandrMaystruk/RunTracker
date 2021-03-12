package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import kotlinx.coroutines.flow.Flow

interface RaceInteractor {

    suspend fun subscribeToUpdates()

    suspend fun getRaceList(): Flow<List<Race>>

    suspend fun getRaceList(query: String): TaskResult<Exception, List<Race>>

    suspend fun saveLastSelectedRaceId(raceId: String): TaskResult<Exception, Unit>

    suspend fun getLastSelectedRaceId(): TaskResult<Exception, String>

}