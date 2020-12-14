package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import java.util.*

interface RunnersInteractor {

    suspend fun getRunner(runnerNumber: Long): TaskResult<Exception, Runner>

    suspend fun getRunners(distanceId: Long, initSize: Int? = null): TaskResult<Exception, List<Runner>>

    suspend fun getFinishers(distanceId: Long): TaskResult<Exception, List<Runner>>

    suspend fun addCurrentCheckpointToRunner(cardId: String): TaskResult<Exception, RunnerChange>

    suspend fun addCurrentCheckpointToRunner(runnerNumber: Long): TaskResult<Exception, RunnerChange>

    suspend fun addStartCheckpointToRunners(date: Date): TaskResult<Exception, Unit>

    suspend fun changeRunnerCardId(runnerNumber: Long, newCardId: String): TaskResult<Exception, RunnerChange>

    suspend fun markRunnerGotOffTheRoute(runnerNumber: Long): TaskResult<Exception, RunnerChange>

    suspend fun removeCheckpointForRunner(runnerNumber: Long, checkpointId: Long): TaskResult<Exception, RunnerChange>

}