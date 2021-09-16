package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.Distance
import kotlinx.coroutines.flow.Flow

interface ProvideDistanceUseCase {

    suspend fun invoke(): Flow<List<Distance>>

}