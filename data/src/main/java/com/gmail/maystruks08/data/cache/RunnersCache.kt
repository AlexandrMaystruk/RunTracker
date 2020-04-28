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
}