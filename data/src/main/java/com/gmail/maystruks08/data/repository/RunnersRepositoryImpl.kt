package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.CheckpointsCache
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class RunnersRepositoryImpl @Inject constructor(
    private val runnersCache: RunnersCache,
    private val checkpointsCache: CheckpointsCache
) : RunnersRepository {

    override suspend fun getAllRunners() = runnersCache.runnersList

    override suspend fun getRunnerById(cardId: String): Runner? =
        runnersCache.runnersList.find { it.id == cardId }

    override suspend fun updateRunnerData(runner: Runner): Runner? {
        val index = runnersCache.runnersList.indexOfFirst { it.id == runner.id }
        if (index != -1) {
            runnersCache.runnersList.removeAt(index)
        }
        runnersCache.runnersList.add(runner)
        return runner
    }

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint {
        return when (type) {
            RunnerType.NORMAL -> checkpointsCache.currentCheckpoint
            RunnerType.IRON -> checkpointsCache.currentIronPeopleCheckpoint
        }
    }
}