package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.repository.RunnersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SubscribeToRunnersUpdateUseCaseImpl @Inject constructor(
    private val repository: RunnersRepository,
) : SubscribeToRunnersUpdateUseCase {

    override suspend fun invoke(): Flow<Unit> {
        val currentRaceId = repository.getRaceId()
        return repository
            .observeRunnerData(currentRaceId)
            .flowOn(Dispatchers.IO)
    }
}