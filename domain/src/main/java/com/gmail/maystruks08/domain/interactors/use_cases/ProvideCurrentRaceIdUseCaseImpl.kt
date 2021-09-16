package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class ProvideCurrentRaceIdUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository
) : ProvideCurrentRaceIdUseCase {

    override suspend fun invoke(): String {
        return runnersRepository.getRaceId()
    }

}

