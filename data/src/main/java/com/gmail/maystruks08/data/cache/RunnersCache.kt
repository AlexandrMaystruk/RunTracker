package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunnersCache @Inject constructor() {

    var normalRunnersList = mutableListOf<Runner>()

    var ironRunnersList = mutableListOf<Runner>()

    fun getRunnerList(type: RunnerType): MutableList<Runner> = when (type) {
        RunnerType.NORMAL -> normalRunnersList
        RunnerType.IRON -> ironRunnersList
    }

    fun findRunnerByCardId(cardId: String): Runner? =
        normalRunnersList.find { it.cardId == cardId } ?: ironRunnersList.find { it.cardId == cardId }

    fun findRunnerByNumber(runnerNumber: Int): Runner? =
        normalRunnersList.find { it.number == runnerNumber } ?: ironRunnersList.find { it.number == runnerNumber }

    fun findRunnerTeamMembers(currentRunnerNumber: Int, teamName: String): List<Runner>? {
        val result = normalRunnersList.filter { it.teamName == teamName && it.number != currentRunnerNumber }
        if (result.any { it.isOffTrack }) return null
        return result
    }

}