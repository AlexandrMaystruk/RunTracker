package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow

interface DistanceRepository {

    suspend fun observeDistanceData(raceId: Long): Flow<Change<Distance>>

    suspend fun getDistanceList(raceId: Long): List<Distance>


}