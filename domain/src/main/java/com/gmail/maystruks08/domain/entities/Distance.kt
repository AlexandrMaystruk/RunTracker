package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner

data class Distance(
    val id: Long,
    val name: String,
    val authorId: Long,
    val checkpoints: MutableList<Checkpoint>,
    val runners: MutableList<Runner>)
