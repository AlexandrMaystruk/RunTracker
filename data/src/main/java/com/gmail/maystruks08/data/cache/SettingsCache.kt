package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class SettingsCache @Inject constructor() {

    var checkpoints = listOf<Checkpoint>()

    var checkpointsIronPeople = listOf<Checkpoint>()

    lateinit var currentCheckpoint: Checkpoint

    lateinit var currentIronPeopleCheckpoint: Checkpoint

    var dateOfStart: Date? = null

    var adminUserIds = ArrayList<String>()

    fun getCheckpointList(type: RunnerType): List<Checkpoint> = when (type) {
        RunnerType.NORMAL -> checkpoints
        RunnerType.IRON -> checkpointsIronPeople
    }

    fun getCurrentCheckpoint(type: RunnerType): Checkpoint = when (type) {
        RunnerType.NORMAL -> currentCheckpoint
        RunnerType.IRON -> currentIronPeopleCheckpoint
    }

}