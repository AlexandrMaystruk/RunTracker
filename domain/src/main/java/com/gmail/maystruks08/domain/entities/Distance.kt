package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

data class Distance(
    val id: Long,
    val raceId: Long,
    val name: String,
    val authorId: Long,
    val dateOfStart: Date,
    val checkpoints: MutableList<Checkpoint>,
    val runners: MutableSet<Runner> = sortedSetOf(
        compareBy<Runner> { it.totalResult }
            .thenBy { it.isOffTrack }
            .thenBy { runner -> runner.checkpoints.count { it.getResult() != null } }
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
        val result = runners.filter { it.teamName == teamName && it.number != currentRunnerNumber }
        if (result.any { it.isOffTrack }) return null
        return result
    }
}
