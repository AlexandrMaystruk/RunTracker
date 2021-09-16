package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class ProvideRunnerUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository
) : ProvideRunnerUseCase {

    override suspend fun invoke(runnerNumber: String): Runner {
        return runnersRepository.getRunnerByNumber(runnerNumber)?: throw RunnerNotFoundException()
    }

}