package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointInteractor {

    suspend fun getCheckpoints(distanceId: Long): TaskResult<Exception, List<Checkpoint>>

}