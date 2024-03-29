package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Runner
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ManageRunnerCheckpointInteractor {

    suspend fun addCurrentCheckpointByCardId(cardId: String): IRunner

    suspend fun addCurrentCheckpointByNumber(runnerNumber: String): IRunner

    suspend fun addStartCheckpoint(currentDistance: Distance)

    suspend fun removeCheckpoint(runnerNumber: String, checkpointId: String): Change<IRunner>

}