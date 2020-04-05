package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*

interface RunnersRepository {

    suspend fun getAllRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>>

    suspend fun getRunnerFinishers(): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun getRunnerById(cardId: String): Runner?

    suspend fun updateRunnerData(runner: Runner): Runner?

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

    suspend fun finishWork()

}