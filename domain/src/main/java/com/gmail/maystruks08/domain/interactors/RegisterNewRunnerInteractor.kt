package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import java.util.*


interface RegisterNewRunnerInteractor {

    suspend fun registerNewRunners(registerInputData: List<RegisterInputData>): ResultOfTask<Exception, Unit>

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