package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import java.util.*

data class Team(
    val teamName: String,
    val runners: List<Runner>,
    val distanceType: DistanceType
) : IRunner {

    override val id get() = teamName
    override var lastAddedCheckpoint: Checkpoint? = null
    override val actualDistanceId: String get() = runners.firstOrNull()?.actualDistanceId ?: DEF_STRING_VALUE

    val result: Date?
        get() {
            return when (distanceType) {
                DistanceType.REPLAY -> {
                    if (runners.any { it.offTrackDistance == it.actualDistanceId }) return null
                    val hasUncompletedCheckpoints = runners.any { runner ->
                        runner.currentCheckpoints.any { it is CheckpointImpl }
                    }
                    if (hasUncompletedCheckpoints) return null
                    if (runners.any { it.result == null }) return null
                    var totalTime = 0L
                    runners.forEach {
                        totalTime += it.result?.time ?: 0L
                    }
                    Date(totalTime)
                }
                DistanceType.TEAM -> {
                    if (runners.any { it.offTrackDistance == it.actualDistanceId }) return null
                    if (runners.any { it.result == null }) return null
                    runners.minByOrNull { it.result?.time ?: Long.MAX_VALUE }?.result
                }
                else -> null
            }
        }


    override fun getTotalResult(): Date? {
        return result
    }

    override fun checkIsOffTrack(): Boolean {
        return runners.any { it.checkIsOffTrack() }
    }

    override fun restart(checkpoint: Checkpoint) {
        runners.forEach { it.restart(checkpoint) }
    }

    override fun getPassedCheckpointCount(): Int {
        return runners.sumBy { it.currentCheckpoints.count() }
    }
}
