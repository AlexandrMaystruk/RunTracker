package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.*

interface RegisterNewRunnersRepository {

    suspend fun saveNewRunner(runner: Runner)

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

}