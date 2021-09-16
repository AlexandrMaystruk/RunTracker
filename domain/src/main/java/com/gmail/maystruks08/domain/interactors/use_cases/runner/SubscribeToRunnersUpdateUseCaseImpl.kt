package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class SubscribeToRunnersUpdateUseCaseImpl @Inject constructor(
    private val repository: RunnersRepository,
) : SubscribeToRunnersUpdateUseCase {

    override suspend fun invoke() {
        val currentRaceId = repository.getRaceId()
        repository.observeRunnerData(currentRaceId)
    }
}