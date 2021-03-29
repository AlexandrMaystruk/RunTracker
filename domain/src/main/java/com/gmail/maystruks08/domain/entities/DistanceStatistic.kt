package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.runner.Runner

data class DistanceStatistic(var runnerCountInProgress: Int = 0, var runnerCountOffTrack: Int = 0, var finisherCount: Int = 0){

    fun calculateStatistic(distanceId: String, distanceRunners: List<Runner>){
        runnerCountInProgress = distanceRunners.size
        distanceRunners.forEach { runner ->
            when {
                runner.offTrackDistances.any { it == runner.actualDistanceId} -> {
                    runnerCountInProgress --
                    runnerCountOffTrack++
                }
                runner.getCheckpointCount() == runner.getPassedCheckpointCount() -> {
                    finisherCount++
                    runnerCountInProgress --
                }
            }
        }
    }
}