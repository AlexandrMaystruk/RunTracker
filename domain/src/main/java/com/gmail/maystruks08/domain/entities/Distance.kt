package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner

data class Distance(
    val name: String,
    val checkpoints: List<Checkpoint>,
    val currentCheckpoint: Checkpoint,
    val runners: List<Runner>)
