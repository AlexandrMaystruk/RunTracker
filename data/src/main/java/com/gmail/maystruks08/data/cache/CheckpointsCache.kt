package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointSettings
import com.gmail.maystruks08.domain.entities.CheckpointState
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckpointsCache @Inject constructor() {

    var checkpointsList = listOf(
        Checkpoint("С", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("15", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("46", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("52", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("70", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("81", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("90", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("Ф", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())))

    var checkpointsIronPeopleList = listOf(
        Checkpoint("С", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("7.5", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("15", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("42", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("52", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("70", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("81", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("91", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("Ф", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())))

    var currentCheckpoint: Checkpoint = checkpointsList[0]

    var currentIronPeopleCheckpoint: Checkpoint = checkpointsList[0]


}