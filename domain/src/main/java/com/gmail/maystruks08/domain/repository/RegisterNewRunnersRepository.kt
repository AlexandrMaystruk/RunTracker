package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*

interface RegisterNewRunnersRepository {

    suspend fun saveNewRunner(runner: Runner): ResultOfTask<Exception, Unit>

    suspend fun getCheckpoints(runnerType: RunnerType): List<Checkpoint>

}