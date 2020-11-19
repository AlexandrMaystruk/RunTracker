package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnerInteractorImpl @Inject constructor(
    private val runnersRepository: RegisterNewRunnersRepository
) : RegisterNewRunnerInteractor {

    override suspend fun registerNewRunners(registerInputData: List<RegisterNewRunnerInteractor.RegisterInputData>): ResultOfTask<Exception, Unit> {
        return ResultOfTask.build {
            val runnerType = registerInputData.first().runnerType
            val checkpoints = runnersRepository.getCheckpoints(runnerType).toMutableList()
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
                    type = runnerType,
                    totalResult = null,
                    checkpoints = checkpoints,
                    isOffTrack = false
                )
            }
            runnersRepository.saveNewRunners(runners)
        }
    }
}