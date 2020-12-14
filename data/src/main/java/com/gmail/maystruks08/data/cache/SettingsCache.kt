package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class SettingsCache @Inject constructor() {

    var checkpoints = listOf<CheckpointImpl>()

    var checkpointsIronPeople = listOf<CheckpointImpl>()

    lateinit var currentCheckpoint: CheckpointImpl

    lateinit var currentIronPeopleCheckpoint: CheckpointImpl

    var dateOfStart: Date? = null

    var adminUserIds = ArrayList<String>()

    fun getCheckpointList(type: RunnerType): List<CheckpointImpl> = when (type) {
        RunnerType.NORMAL -> checkpoints
        RunnerType.IRON -> checkpointsIronPeople
    }

    fun getCurrentCheckpoint(type: RunnerType): CheckpointImpl = when (type) {
        RunnerType.NORMAL -> currentCheckpoint
        RunnerType.IRON -> currentIronPeopleCheckpoint
    }

}