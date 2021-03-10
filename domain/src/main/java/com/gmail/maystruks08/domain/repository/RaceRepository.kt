package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Race
import kotlinx.coroutines.flow.Flow

interface RaceRepository {

    suspend fun subscribeToUpdates(): Flow<Unit>

    suspend fun getRaceList(): List<Race>

    suspend fun saveRace(race: Race)

    suspend fun saveLastSelectedRaceId(raceId: String)

    suspend fun getLastSelectedRaceId(): String

}