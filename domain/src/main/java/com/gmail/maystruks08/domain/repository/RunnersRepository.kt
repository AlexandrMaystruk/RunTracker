package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException

interface RunnersRepository {

    suspend fun getRunners(distanceId: String, onlyFinishers: Boolean = false, initSize: Int? = null): List<Runner>

    suspend fun getRunnerByCardId(cardId: String): Runner?

    suspend fun getRunnerByNumber(runnerNumber: Long): Runner?

    suspend fun getRunnerTeamMembers(currentRunnerNumber: Long, teamName: String): List<Runner>?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

}