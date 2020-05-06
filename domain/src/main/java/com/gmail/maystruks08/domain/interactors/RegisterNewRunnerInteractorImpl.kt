package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import java.util.*
import javax.inject.Inject

class RegisterNewRunnerInteractorImpl @Inject constructor(private val runnersRepository: RegisterNewRunnersRepository) :
    RegisterNewRunnerInteractor {

    override suspend fun registerNewRunner(fullName: String, runnerSex: RunnerSex, dateOfBirthday: Date,
        city: String, runnerNumber: Int, runnerType: RunnerType, runnerCardId: String): ResultOfTask<Exception, Unit> {
        return ResultOfTask.build {
            val checkpoints = runnersRepository.getCheckpoints(runnerType).toMutableList()
            val newRunner = Runner(
                id = runnerCardId,
                fullName = fullName,
                number = runnerNumber,
                sex = runnerSex,
                city = city,
                dateOfBirthday = dateOfBirthday,
                type = runnerType,
                totalResult = null,
                checkpoints = checkpoints
            )
            runnersRepository.saveNewRunner(newRunner)
        }
    }
}