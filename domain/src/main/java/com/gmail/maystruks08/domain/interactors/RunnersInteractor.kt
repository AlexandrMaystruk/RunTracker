package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

interface RunnersInteractor {

    suspend fun getRunner(runnerNumber: Long): TaskResult<Exception, Runner>

    suspend fun getRunners(
        distanceId: Long,
        initSize: Int? = null
    ): TaskResult<Exception, List<Runner>>

    suspend fun getFinishers(distanceId: Long): TaskResult<Exception, List<Runner>>

    suspend fun addCurrentCheckpointToRunner(cardId: String): TaskResult<Exception, Runner>

    suspend fun addCurrentCheckpointToRunner(runnerNumber: Long): TaskResult<Exception, Runner>

    suspend fun addStartCheckpointToRunners(date: Date): TaskResult<Exception, Unit>

    suspend fun changeRunnerCardId(
        runnerNumber: Long,
        newCardId: String
    ): TaskResult<Exception, Change<Runner>>

    suspend fun markRunnerGotOffTheRoute(runnerNumber: Long): TaskResult<Exception, Runner>

    suspend fun removeCheckpointForRunner(
        runnerNumber: Long,
        checkpointId: Long
    ): TaskResult<Exception, Change<Runner>>

}