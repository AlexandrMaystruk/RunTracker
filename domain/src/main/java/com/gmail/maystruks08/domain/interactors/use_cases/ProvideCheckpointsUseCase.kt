package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface ProvideCheckpointsUseCase {

    suspend fun invoke(
        distanceId: String
    ): List<Checkpoint>

}