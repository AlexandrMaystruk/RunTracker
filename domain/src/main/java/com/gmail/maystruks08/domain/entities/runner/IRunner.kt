package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import java.util.*

interface IRunner {

    val id: String
    val actualDistanceId: String

    fun getPassedCheckpointCount(): Int

    var lastAddedCheckpoint: Checkpoint?

    fun getTotalResult(): Date?
    fun checkIsOffTrack(): Boolean
}