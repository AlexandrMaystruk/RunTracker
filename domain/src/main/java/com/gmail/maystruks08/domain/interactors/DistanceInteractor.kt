package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow

interface DistanceInteractor {

    suspend fun observeDistanceDataFlow(raceId: String)

    suspend fun getDistancesFlow(raceId: String): Flow<List<Distance>>

    suspend fun provideCurrentSelectedRaceId(): String

}