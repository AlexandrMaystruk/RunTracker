package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.RunnerType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsCache @Inject constructor() {

    var checkpoints = listOf(
        Checkpoint(0, "С"),
        Checkpoint(1, "15"),
        Checkpoint(2, "46"),
        Checkpoint(3, "52"),
        Checkpoint(4, "70"),
        Checkpoint(5, "81"),
        Checkpoint(6, "90"),
        Checkpoint(7, "Ф")
    )

    var checkpointsIronPeople = listOf(
        Checkpoint(0, "С"),
        Checkpoint(1, "7.5"),
        Checkpoint(2, "15"),
        Checkpoint(3, "42"),
        Checkpoint(4, "52"),
        Checkpoint(5, "70"),
        Checkpoint(6, "81"),
        Checkpoint(7, "91"),
        Checkpoint(8, "Ф")
    )

    var currentCheckpoint: Checkpoint = checkpoints[0]

    var currentIronPeopleCheckpoint: Checkpoint = checkpoints[0]

    var dateOfStart: Date? = null

    fun getCheckpointList(type: RunnerType): List<Checkpoint> = when (type) {
        RunnerType.NORMAL -> checkpoints
        RunnerType.IRON -> checkpointsIronPeople
    }

    fun getCurrentCheckpoint(type: RunnerType): Checkpoint = when (type) {
        RunnerType.NORMAL -> currentCheckpoint
        RunnerType.IRON -> currentIronPeopleCheckpoint
    }

}