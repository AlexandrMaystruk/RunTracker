package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.runner.Runner

data class Statistic(val distanceId: String, var runnerCountInProgress: Int = 0, var runnerCountOffTrack: Int = 0, var finisherCount: Int = 0){

    fun calculateStatistic(distanceRunners: List<Runner>){
        runnerCountInProgress = distanceRunners.size
        distanceRunners.forEach { runner ->
            when {
                runner.offTrackDistance == runner.actualDistanceId -> {
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