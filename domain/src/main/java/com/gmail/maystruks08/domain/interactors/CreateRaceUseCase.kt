package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import java.util.*

interface CreateRaceUseCase {

    suspend fun invoke(name: String, startDate: Date): TaskResult<Exception, Unit>

}