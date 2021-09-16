package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.repository.RunnersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideRunnersUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val logHelper: LogHelper
) : ProvideRunnersUseCase {

    override suspend fun invoke(
        distanceId: String,
        distanceType: DistanceType,
        query: String?
    ): Flow<List<IRunner>> {
       return when (distanceType) {
            DistanceType.MARATHON -> {
                runnersRepository.getRunnersFlow(distanceId = distanceId, onlyFinishers = false, query = query)
                    .map { list -> list.sortedByDescending { it.getPassedCheckpointCount() } }
            }
            DistanceType.REPLAY, DistanceType.TEAM -> {
                runnersRepository
                    .getTeamRunnersFlow(distanceId)
                    .map { list -> list.sortedByDescending { it.getPassedCheckpointCount() } }
            }
        }.flowOn(Dispatchers.IO)
    }
}