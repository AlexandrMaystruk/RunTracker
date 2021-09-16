package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.runner.IRunner
import kotlinx.coroutines.flow.Flow

interface ProvideRunnersUseCase {

    suspend fun invoke(distanceId: String, distanceType: DistanceType, query: String?): Flow<List<IRunner>>

}
