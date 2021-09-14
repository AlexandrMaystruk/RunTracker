package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.repository.RunnersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProvideFinishersUseCaseImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val logHelper: LogHelper
) : ProvideFinishersUseCase {

    override suspend fun invoke(distanceId: String): Flow<List<IRunner>> {
        return runnersRepository.getRunnersFlow(distanceId = distanceId, onlyFinishers = true)
            .map { list ->
                val sorted = mutableListOf<IRunner>()
                list.groupBy { it.currentTeamName }.forEach {
                    val teamName = it.key
                    if (teamName.isNullOrEmpty()) {
                        sorted.addAll(it.value)
                    } else {
                        sorted.add(Team(teamName, it.value))
                    }
                }
                sorted.sortedByDescending { it.getPassedCheckpointCount() }
            }
    }
}