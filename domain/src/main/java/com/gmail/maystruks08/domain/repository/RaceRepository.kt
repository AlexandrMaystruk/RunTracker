package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Race
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaceRepository {

    suspend fun subscribeToUpdates()

    suspend fun getRaceList(): Flow<List<Race>>

    suspend fun getRaceList(query: String): List<Race>

    suspend fun saveRace(race: Race)

    suspend fun saveLastSelectedRaceId(raceId: String)

    suspend fun getLastSelectedRaceId(): String

}