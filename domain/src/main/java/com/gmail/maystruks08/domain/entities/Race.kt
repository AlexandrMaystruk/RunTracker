package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

data class Race(
    val id: Long,
    val name: String,
    val dateCreation: Date,
    val registrationIsOpen: Boolean,
    val authorId: Long,
    val distanceList: MutableList<Distance>
) {

    fun findDistance(distanceId: Long): Distance? {
        return distanceList.firstOrNull { it.id == distanceId }
    }

    fun findRunner(distanceId: Long, runnerId: Long): Runner? {
        return findDistance(distanceId)?.findRunnerById(runnerId)
    }

    fun findRunnerByCardId(distanceId: Long, cardId: String): Runner? {
        return findDistance(distanceId)?.findRunnerByCardId(cardId)
    }

    fun findRunnerTeamMembers(
        distanceId: Long,
        currentRunnerNumber: Long,
        teamName: String
    ): List<Runner>? {
        return findDistance(distanceId)?.findRunnerTeamMembers(currentRunnerNumber, teamName)
    }

    fun createNewDistance(
        id: Long,
        name: String,
        authorId: Long,
        dateOrStart: Date,
        checkpoints: MutableList<Checkpoint>? = null,
    ) {
        val newDistance = Distance(id, name, authorId, dateOrStart, checkpoints ?: mutableListOf(), sortedSetOf(
            compareBy<Runner> { it.totalResult }
                .thenBy { it.isOffTrack }
                .thenBy { runner -> runner.checkpoints.count { it.getResult() != null } }
        ))
        distanceList.add(newDistance)
    }

}
