package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException

interface RunnersRepository{

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun getRunnerById(cardId: String): Runner?

    suspend fun getRunnerTeamMembers(currentRunnerId: String, teamName: String): List<Runner>?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

    suspend fun finishWork()

}