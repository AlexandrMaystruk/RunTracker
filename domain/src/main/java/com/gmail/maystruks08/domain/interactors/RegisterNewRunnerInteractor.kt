package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import java.util.*


interface RegisterNewRunnerInteractor {

    suspend fun registerNewRunner(
        fullName: String,
        runnerSex: RunnerSex,
        dateOfBirthday: Date,
        city: String,
        runnerNumber: Int,
        runnerType: RunnerType,
        runnerCardId: String
    ): ResultOfTask<Exception, Unit>
}