package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface SaveCheckpointsUseCase {

    suspend fun invoke(distanceId: String, editedCheckpoints: List<Checkpoint>): TaskResult<Exception, Unit>

}
