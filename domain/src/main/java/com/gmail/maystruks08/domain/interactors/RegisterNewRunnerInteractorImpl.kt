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

    override suspend fun registerNewRunner(
        fullName: String,
        runnerSex: RunnerSex,
        dateOfBirthday: Date,
        city: String,
        runnerNumber: Int,
        runnerType: RunnerType,
        runnerCardId: String
    ): ResultOfTask<Exception, Unit> {

        val checkpoints = runnersRepository.getCheckpoints(runnerType)
        val newRunner = Runner(
            id = runnerCardId,
            fullName = fullName,
            number = runnerNumber,
            sex = runnerSex,
            city = city,
            dateOfBirthday = dateOfBirthday,
            type = runnerType,
            checkpoints = checkpoints
        )

        return runnersRepository.saveNewRunner(newRunner)
    }
}