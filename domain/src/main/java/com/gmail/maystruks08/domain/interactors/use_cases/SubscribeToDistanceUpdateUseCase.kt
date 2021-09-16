package com.gmail.maystruks08.domain.interactors.use_cases

import kotlinx.coroutines.flow.Flow

interface SubscribeToDistanceUpdateUseCase {

    suspend fun invoke(): Flow<Unit>

}



