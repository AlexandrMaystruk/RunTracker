package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.TaskResult

interface UpdateDistanceNameUseCase {

    suspend fun invoke(distanceId: String, newName: String): TaskResult<Exception, Unit>

}
