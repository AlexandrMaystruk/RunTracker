package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException

interface RunnersRepository {

    suspend fun getRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>>

    suspend fun getNormalRunners(): List<Runner>

    suspend fun getIronRunners(): List<Runner>

    suspend fun getRunnerFinishers(): ResultOfTask<Exception, List<Runner>>

    suspend fun getIronRunnerFinishers(): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun getRunnerById(cardId: String, type: RunnerType): Runner?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    /** return pair with runner checkpoint and iron people checkpoint*/
    suspend fun getStartCheckpoints(): Pair<List<Checkpoint>, List<Checkpoint>>

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

    suspend fun getCheckpointsCount(type: RunnerType): Int

    suspend fun finishWork()

}