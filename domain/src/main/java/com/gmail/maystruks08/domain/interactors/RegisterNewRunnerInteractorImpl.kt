package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnerInteractorImpl @Inject constructor(
    private val runnersRepository: RegisterNewRunnersRepository
) : RegisterNewRunnerInteractor {

    override suspend fun registerNewRunners(registerInputData: List<RegisterNewRunnerInteractor.RegisterInputData>): TaskResult<Exception, Unit> {
        return TaskResult.build {
            val runners = registerInputData.map {
                Runner(
                    cardId = it.runnerCardId,
                    fullName = it.fullName,
                    shortName = it.shortName,
                    phone = it.phone,
                    number = it.runnerNumber,
                    sex = it.runnerSex,
                    city = it.city,
                    dateOfBirthday = it.dateOfBirthday,
                    teamName = it.teamName,
                    totalResult = null,
                    checkpoints = mutableListOf(),
                    isOffTrack = false,
                    distanceIds = mutableListOf(),
                    raceIds = mutableListOf(),
                    actualDistanceId = 0
                )
            }
            runnersRepository.saveNewRunners(runners)
        }
    }
}