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
    val dateOfStart: Date?,
    val checkpoints: MutableList<Checkpoint>,
    val statistic: DistanceStatistic,
    val runners: MutableSet<Runner> = sortedSetOf(
        compareBy<Runner> { it.totalResults[id] }
            .thenBy { runner -> runner.offTrackDistances.any { it == id } }
            .thenBy { runner -> runner.checkpoints[id]?.count { it.getResult() != null } }
    )
) {

    fun addRunner(newRunner: Runner) {
        runners.add(newRunner)
    }

    fun removeRunner(newRunner: Runner) {
        runners.removeAll { newRunner.number == it.number }
    }

    fun findRunnerById(runnerId: String): Runner? {
        return runners.find { it.number == runnerId }
    }

    fun findRunnerByCardId(cardId: String): Runner? {
        return runners.find { it.cardId == cardId }
    }

    fun findRunnerTeamMembers(currentRunnerNumber: String, teamName: String): List<Runner>? {
        return runners.filter { runner ->
            if (runner.offTrackDistances.any { it == id }) return null
            runner.teamNames[id] == teamName && runner.number != currentRunnerNumber
        }
    }
}
