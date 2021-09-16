package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import kotlinx.coroutines.flow.Flow

interface RunnersRepository {

    suspend fun observeRunnerData(raceId: String): Flow<Unit>

    suspend fun getRunners(
        distanceId: String,
        query: String,
        onlyFinishers: Boolean = false
    ): List<Runner>

    suspend fun getRunnersFlow(
        distanceId: String,
        onlyFinishers: Boolean = false,
        query: String? = null
    ): Flow<List<Runner>>

    suspend fun getTeamRunnersFlow(
        distanceId: String,
        onlyFinishers: Boolean = false
    ): Flow<List<Team>>

    suspend fun getRunnerByCardId(cardId: String): Runner?

    suspend fun getRunnerByNumber(runnerNumber: String): Runner?

    suspend fun getRunnerTeamMembers(currentRunnerNumber: String, teamName: String): List<Runner>?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    suspend fun getRaceId(): String


}