package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DistanceRepository {

    suspend fun observeDistanceDataFlow(raceId: String): Flow<Unit>

    suspend fun getDistanceListFlow(raceId: String): Flow<List<Distance>>

    suspend fun getDistanceFlow(raceId: String, distanceId: String): Flow<Distance>

    //return pair. First raceId second race name
    suspend fun getLastSelectedRace(): Pair<String, String>

    suspend fun updateDistanceName(distanceId: String, newName: String)

    suspend fun updateDistanceStartDate(distanceId: String, startDate: Date?)

}