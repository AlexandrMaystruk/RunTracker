package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointType
import javax.inject.Inject

class CheckpointInteractorImpl @Inject constructor(): CheckpointInteractor {

    override suspend fun getCheckpoints(distanceId: Long): TaskResult<Exception, List<Checkpoint>> {
        return TaskResult.build {
            listOf(
                Checkpoint(0, "Start", CheckpointType.NORMAL),
                Checkpoint(1, "1", CheckpointType.NORMAL),
                Checkpoint(2, "Finish", CheckpointType.NORMAL)
            )
        }
    }
}