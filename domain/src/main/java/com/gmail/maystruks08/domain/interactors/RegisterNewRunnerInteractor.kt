package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import java.util.*


interface RegisterNewRunnerInteractor {

    suspend fun registerNewRunners(registerInputData: List<RegisterInputData>): TaskResult<Exception, Unit>

    data class RegisterInputData(
        var fullName: String,
        var shortName: String,
        var phone: String,
        var runnerSex: RunnerSex,
        var dateOfBirthday: Date,
        var city: String,
        var runnerNumber: Int,
        var runnerType: RunnerType,
        var runnerCardId: String,
        var teamName: String?
    )
}