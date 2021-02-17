package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult

interface DistanceInteractor {

    suspend fun getDistances(): TaskResult<Exception, List<Distance>>

}