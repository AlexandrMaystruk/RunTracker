package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface IRunner {

    val id: String
    val actualDistanceId: String

    fun getPassedCheckpointCount(): Int

    var lastAddedCheckpoint: Checkpoint?
}