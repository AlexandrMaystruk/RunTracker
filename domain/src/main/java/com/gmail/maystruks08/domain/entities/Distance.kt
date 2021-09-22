package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

data class Distance(
    val id: String,
    val raceId: String,
    val name: String,
    val type: DistanceType,
    val authorId: String,
    var dateOfStart: Date?,
    val checkpoints: MutableList<Checkpoint>,
    val statistic: Statistic
)
