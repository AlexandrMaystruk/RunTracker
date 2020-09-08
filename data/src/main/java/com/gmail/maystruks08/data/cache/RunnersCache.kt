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

    fun findRunner(id: String): Runner? =
        normalRunnersList.find { it.id == id } ?: ironRunnersList.find { it.id == id }

    fun findRunnerTeamMembers(currentRunnerId: String, teamName: String): List<Runner>? =
        normalRunnersList.filter { it.teamName == teamName && it.id != currentRunnerId }
}