package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.CheckpointStatistic
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CalculateCheckpointStatisticUseCase {

    suspend fun invoke(checkpoints: MutableList<Checkpoint>): List<CheckpointStatistic>

}