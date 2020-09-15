package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.RunnerType
import java.util.*

interface RunnersInteractor {

    suspend fun getRunner(runnerNumber: Int): ResultOfTask<Exception, Runner>

    suspend fun getRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>>

    suspend fun getFinishers(type: RunnerType): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit)

    suspend fun addCurrentCheckpointToRunner(cardId: String): ResultOfTask<Exception, RunnerChange>

    suspend fun addCurrentCheckpointToRunner(runnerNumber: Int): ResultOfTask<Exception, RunnerChange>

    suspend fun addStartCheckpointToRunners(date: Date)

    suspend fun changeRunnerCardId(runnerNumber: Int, newCardId: String): ResultOfTask<Exception, RunnerChange>

    suspend fun markRunnerGotOffTheRoute(runnerNumber: Int): ResultOfTask<Exception, RunnerChange>

    suspend fun removeCheckpointForRunner(runnerNumber: Int, checkpointId: Int): ResultOfTask<Exception, RunnerChange>

    suspend fun finishWork()

}