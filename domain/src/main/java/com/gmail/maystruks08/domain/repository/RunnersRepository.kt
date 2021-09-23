package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import kotlinx.coroutines.flow.Flow

interface RunnersRepository {

    suspend fun observeRunnerData(raceId: String): Flow<Unit>

    suspend fun getRunnersFlow(
        distance: Distance,
        onlyFinishers: Boolean = false,
        query: String? = null
    ): Flow<List<Runner>>

    suspend fun getTeamRunnersFlow(
        distance: Distance,
        onlyFinishers: Boolean = false,
        query: String? = null
    ): Flow<List<Team>>

    suspend fun getRunnerByCardId(cardId: String): Runner?

    suspend fun getRunnerByNumber(runnerNumber: String): Runner?

    suspend fun getTeam(teamName: String): Team?

    @Throws(SaveRunnerDataException::class, SyncWithServerException::class)
    suspend fun updateRunnerData(runner: Runner): Runner

    suspend fun getRaceId(): String


}