package com.gmail.maystruks08.domain.interactors.use_cases.runner

import kotlinx.coroutines.flow.Flow

interface SubscribeToRunnersUpdateUseCase {

    suspend fun invoke(): Flow<Unit>

}
