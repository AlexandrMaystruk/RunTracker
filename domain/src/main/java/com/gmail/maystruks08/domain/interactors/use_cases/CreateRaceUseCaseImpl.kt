package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.use_cases.CreateRaceUseCase
import com.gmail.maystruks08.domain.repository.RaceRepository
import com.gmail.maystruks08.domain.toServerFormat
import java.util.*
import javax.inject.Inject

class CreateRaceUseCaseImpl @Inject constructor(
    private val raceRepository: RaceRepository
) : CreateRaceUseCase {

    override suspend fun invoke(name: String, startDate: Date): TaskResult<Exception, Unit> {
        return TaskResult.build {
            val authorId = "wefwecewrcwev"
            val newRace = Race(Date().toServerFormat(), name, startDate, true, authorId, mutableListOf(), mutableListOf())
            raceRepository.saveRace(newRace)
        }
    }
}