package com.gmail.maystruks08.domain.entities

data class CheckpointStatistic(
    val checkpointId: String,
    val checkpointName: String,
    val runnerCountInProgress: Int,
    val runnerCountWhoVisitCheckpoint: Int
)