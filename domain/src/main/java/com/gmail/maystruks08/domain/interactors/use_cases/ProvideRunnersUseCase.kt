package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.runner.IRunner
import kotlinx.coroutines.flow.Flow

interface ProvideRunnersUseCase {

    suspend fun invoke(distance: Distance, query: String?): Flow<List<IRunner>>

}
