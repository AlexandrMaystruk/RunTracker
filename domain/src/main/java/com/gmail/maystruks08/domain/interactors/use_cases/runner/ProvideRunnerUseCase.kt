package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.entities.runner.IRunner

interface ProvideRunnerUseCase {

    suspend fun invoke(runnerNumber: String): IRunner

}