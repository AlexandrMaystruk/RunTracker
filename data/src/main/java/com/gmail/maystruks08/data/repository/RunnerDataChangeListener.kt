package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.domain.entities.RunnerChange
import kotlinx.coroutines.flow.Flow

interface RunnerDataChangeListener {

    suspend fun observeRunnerData(): Flow<RunnerChange>

}