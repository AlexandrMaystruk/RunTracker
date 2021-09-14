package com.gmail.maystruks08.domain.entities.runner

data class Team(
    val teamName: String,
    val runners: List<Runner>,
) : IRunner {

    val result: String?
        get() {
            return null
        }

    override fun getPassedCheckpointCount(): Int {
        return runners.sumBy { it.currentCheckpoints?.count() ?: 0 }
    }
}
