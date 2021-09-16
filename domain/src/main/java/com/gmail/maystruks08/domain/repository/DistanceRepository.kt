package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow

interface DistanceRepository {

    suspend fun observeDistanceDataFlow(raceId: String): Flow<Unit>

    suspend fun getDistanceListFlow(raceId: String): Flow<List<Distance>>

    //return pair. First raceId second race name
    suspend fun getLastSelectedRace(): Pair<String, String>

    suspend fun updateDistanceName(distanceId: String, newName: String)

}