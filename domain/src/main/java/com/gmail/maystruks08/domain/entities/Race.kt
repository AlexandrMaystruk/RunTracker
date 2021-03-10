package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import java.util.*

data class Race(
    val id: String,
    val name: String,
    val dateCreation: Date,
    val registrationIsOpen: Boolean,
    val authorId: String,
    val adminListIds: MutableList<String>,
    val distanceList: MutableList<Distance>
) {

    fun findDistance(distanceId: String): Distance? {
        return distanceList.firstOrNull { it.id == distanceId }
    }

    fun findRunner(distanceId: String, runnerId: Long): Runner? {
        return findDistance(distanceId)?.findRunnerById(runnerId)
    }

    fun findRunnerByCardId(distanceId: String, cardId: String): Runner? {
        return findDistance(distanceId)?.findRunnerByCardId(cardId)
    }

    fun findRunnerTeamMembers(
        distanceId: String,
        currentRunnerNumber: Long,
        teamName: String
    ): List<Runner>? {
        return findDistance(distanceId)?.findRunnerTeamMembers(currentRunnerNumber, teamName)
    }

    fun createNewDistance(
        id: String,
        raceId: String,
        name: String,
        authorId: String,
        dateOrStart: Date,
        checkpoints: MutableList<Checkpoint>? = null,
    ) {
        val newDistance = Distance(id,
            raceId,
            name,
            authorId,
            dateOrStart,
            checkpoints ?: mutableListOf(),
            sortedSetOf(
                compareBy<Runner> { it.totalResults[id] }
                    .thenBy { it.isOffTrack[id] }
                    .thenBy { runner -> runner.checkpoints[id]?.count { it.getResult() != null } }
            ))
        distanceList.add(newDistance)
    }

}
