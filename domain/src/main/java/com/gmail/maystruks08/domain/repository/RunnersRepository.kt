package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException

interface RunnersRepository {

    suspend fun getRunners(type: RunnerType, onlyFinishers: Boolean = false): List<Runner>

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun getRunnerByCardId(cardId: String): Runner?

    suspend fun getRunnerByNumber(runnerNumber: Int): Runner?

    suspend fun getRunnerTeamMembers(currentRunnerNumber: Int, teamName: String): List<Runner>?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

    suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint

    suspend fun finishWork()

}