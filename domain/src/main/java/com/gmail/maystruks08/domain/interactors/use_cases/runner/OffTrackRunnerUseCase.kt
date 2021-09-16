package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.entities.runner.Runner

interface OffTrackRunnerUseCase {

    suspend fun invoke(runnerNumber: String): Runner

}