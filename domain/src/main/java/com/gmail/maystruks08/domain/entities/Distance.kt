package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

data class Distance(
    val id: String,
    val raceId: String,
    val name: String,
    val authorId: String,
    val dateOfStart: Date,
    val checkpoints: MutableList<Checkpoint>,
    val statistic: DistanceStatistic,
    val runners: MutableSet<Runner> = sortedSetOf(
        compareBy<Runner> { it.totalResults[id] }
            .thenBy { it.isOffTrack[id] }
            .thenBy { runner -> runner.checkpoints[id]?.count { it.getResult() != null } }
    )
) {

    fun addRunner(newRunner: Runner) {
        runners.add(newRunner)
    }

    fun removeRunner(newRunner: Runner) {
        runners.removeAll { newRunner.number == it.number }
    }

    fun findRunnerById(runnerId: Long): Runner? {
        return runners.find { it.number == runnerId }
    }

    fun findRunnerByCardId(cardId: String): Runner? {
        return runners.find { it.cardId == cardId }
    }

    fun findRunnerTeamMembers(currentRunnerNumber: Long, teamName: String): List<Runner>? {
        return runners.filter {
            if (it.isOffTrack[id] == true) return null
            it.teamNames[id] == teamName && it.number != currentRunnerNumber
        }
    }
}
