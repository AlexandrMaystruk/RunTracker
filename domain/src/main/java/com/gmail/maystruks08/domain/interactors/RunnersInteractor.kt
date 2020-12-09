package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import java.util.*

interface RunnersInteractor {

    suspend fun getRunner(runnerNumber: Int): TaskResult<Exception, Runner>

    suspend fun getRunners(type: RunnerType, initSize: Int? = null): TaskResult<Exception, List<Runner>>

    suspend fun getFinishers(type: RunnerType): TaskResult<Exception, List<Runner>>

    suspend fun addCurrentCheckpointToRunner(cardId: String): TaskResult<Exception, RunnerChange>

    suspend fun addCurrentCheckpointToRunner(runnerNumber: Int): TaskResult<Exception, RunnerChange>

    suspend fun addStartCheckpointToRunners(date: Date): TaskResult<Exception, Unit>

    suspend fun changeRunnerCardId(runnerNumber: Int, newCardId: String): TaskResult<Exception, RunnerChange>

    suspend fun markRunnerGotOffTheRoute(runnerNumber: Int): TaskResult<Exception, RunnerChange>

    suspend fun removeCheckpointForRunner(runnerNumber: Int, checkpointId: Int): TaskResult<Exception, RunnerChange>

}