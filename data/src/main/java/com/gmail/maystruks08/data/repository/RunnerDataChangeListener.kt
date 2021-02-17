package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import kotlinx.coroutines.flow.Flow

interface RunnerDataChangeListener {

    suspend fun observeRunnerData(): Flow<Change<Runner>>

    suspend fun getLastSavedRaceId(): TaskResult<Exception, Long>

}