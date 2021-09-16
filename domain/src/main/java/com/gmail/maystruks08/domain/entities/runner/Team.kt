package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

data class Team(
    val teamName: String,
    val runners: List<Runner>
) : IRunner {

    override val id get() = teamName
    override var lastAddedCheckpoint: Checkpoint? = null
    override val actualDistanceId: String get() = runners.firstOrNull()?.actualDistanceId?:""

    val result: String?
        get() {
            //TODO implement
            return null
        }


    override fun getPassedCheckpointCount(): Int {
        return runners.sumBy { it.currentCheckpoints?.count() ?: 0 }
    }
}
