package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult
import kotlinx.coroutines.flow.Flow

interface DistanceInteractor {

    suspend fun observeDistanceData(raceId: Long): Flow<Change<Distance>>

    suspend fun getDistances(raceId: Long): TaskResult<Exception, List<Distance>>

}