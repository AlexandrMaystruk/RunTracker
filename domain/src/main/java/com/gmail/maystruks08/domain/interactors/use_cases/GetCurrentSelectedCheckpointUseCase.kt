package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException

interface GetCurrentSelectedCheckpointUseCase {

    @Throws(CheckpointNotFoundException::class)
    suspend fun invoke(distanceId: String): Checkpoint

}