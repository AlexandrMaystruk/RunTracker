package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.RunnerType
import java.util.*

interface RunnersInteractor {

    suspend fun getRunner(id: String, type: RunnerType): ResultOfTask<Exception, Runner>

    suspend fun getAllRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun addCurrentCheckpointToRunner(cardId: String, type: RunnerType): ResultOfTask<Exception, RunnerChange>

    suspend fun addStartCheckpointToRunners(date: Date)

    suspend fun removeCheckpointForRunner(cardId: String, checkpointId: Int, type: RunnerType): ResultOfTask<Exception, RunnerChange>

    suspend fun getCheckpointCount(type: RunnerType): Int

    suspend fun finishWork()

}