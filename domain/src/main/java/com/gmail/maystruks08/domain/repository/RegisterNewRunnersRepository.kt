package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*

interface RegisterNewRunnersRepository {

    suspend fun saveNewRunners(runners: List<Runner>)

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

}