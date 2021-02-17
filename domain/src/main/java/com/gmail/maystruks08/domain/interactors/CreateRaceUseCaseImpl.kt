package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.RaceRepository
import java.util.*
import javax.inject.Inject

class CreateRaceUseCaseImpl @Inject constructor(
    private val raceRepository: RaceRepository
) : CreateRaceUseCase {

    override suspend fun invoke(name: String, startDate: Date): TaskResult<Exception, Unit> {
        return TaskResult.build {
            val authorId = 0L
            val newRace = Race(Date().time, name, startDate, true, authorId, mutableListOf(), mutableListOf())
            raceRepository.saveRace(newRace)
        }
    }
}