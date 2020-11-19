package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerType

interface RegisterNewRunnersRepository {

    suspend fun saveNewRunners(runners: List<Runner>)

    suspend fun getCheckpoints(type: RunnerType): List<Checkpoint>

}