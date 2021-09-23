package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.DEBUG
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.repository.RunnersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideFinishersUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val logHelper: LogHelper
) : ProvideFinishersUseCase {

    override suspend fun invoke(
        distance: Distance,
        query: String?
    ): Flow<List<IRunner>> {
       return when (distance.type) {
            DistanceType.MARATHON -> {
                runnersRepository.getRunnersFlow(
                    distance = distance,
                    onlyFinishers = true,
                    query = query
                )
                    .map { list ->
                        logHelper.log(DEBUG, "Received finishers from repository")
                        val result = list.sortedWith(compareBy<IRunner> { it.getTotalResult() }
                            .thenByDescending { runner -> runner.getPassedCheckpointCount() }
                        )
                        logHelper.log(DEBUG, "Sorting finished")
                        result
                    }
            }
            DistanceType.REPLAY, DistanceType.TEAM -> {
                runnersRepository
                    .getTeamRunnersFlow(
                        distance = distance,
                        onlyFinishers = true,
                        query = query
                    )
                    .map { teams -> teams.filter { it.result != null } }
                    .map { list ->
                        list.sortedWith(compareBy<IRunner> { it.getTotalResult() }
                            .thenByDescending { runner -> runner.getPassedCheckpointCount() }
                        )
                    }
            }
        }.flowOn(Dispatchers.IO)
    }
}