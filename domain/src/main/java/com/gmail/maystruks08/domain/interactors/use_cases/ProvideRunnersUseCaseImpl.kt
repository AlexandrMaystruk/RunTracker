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

class ProvideRunnersUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val logHelper: LogHelper
) : ProvideRunnersUseCase {

    override suspend fun invoke(
        distance: Distance,
        query: String?
    ): Flow<List<IRunner>> {
        return when (distance.type) {
            DistanceType.MARATHON -> {
                runnersRepository.getRunnersFlow(
                    distance = distance,
                    onlyFinishers = false,
                    query = query
                )
                    .map { list ->
                        logHelper.log(DEBUG, "Received runners fro repository")
                        val result = list.sortedWith(compareBy<IRunner> { it.getTotalResult() }
                            .thenBy { runner -> runner.checkIsOffTrack() }
                            .thenByDescending { runner -> runner.getPassedCheckpointCount() }
                        )
                        logHelper.log(DEBUG, "Sorting finished")
                        result
                    }
            }
            DistanceType.REPLAY, DistanceType.TEAM -> {
                runnersRepository
                    .getTeamRunnersFlow(distance = distance)
                    .map { list ->
                        list.sortedWith(compareBy<IRunner> { it.getTotalResult() }
                            .thenBy { runner -> runner.checkIsOffTrack() }
                            .thenByDescending { runner -> runner.getPassedCheckpointCount() }
                        )
                    }
            }
        }.flowOn(Dispatchers.IO)
    }
}