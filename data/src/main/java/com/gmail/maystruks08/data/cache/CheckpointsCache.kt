package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckpointsCache @Inject constructor() {

    var checkpoints = listOf(
        Checkpoint(0,"С", CheckpointState.STEP_UNDO),
        Checkpoint(1,"15", CheckpointState.STEP_UNDO),
        Checkpoint(2,"46", CheckpointState.STEP_UNDO),
        Checkpoint(3,"52", CheckpointState.STEP_UNDO),
        Checkpoint(4,"70", CheckpointState.STEP_UNDO),
        Checkpoint(5,"81", CheckpointState.STEP_UNDO),
        Checkpoint(6,"90", CheckpointState.STEP_UNDO),
        Checkpoint(7,"Ф", CheckpointState.STEP_UNDO)
    )

    var checkpointsIronPeople = listOf(
        Checkpoint(0,"С", CheckpointState.STEP_UNDO),
        Checkpoint(1,"7.5", CheckpointState.STEP_UNDO),
        Checkpoint(2,"15", CheckpointState.STEP_UNDO),
        Checkpoint(3,"42", CheckpointState.STEP_UNDO),
        Checkpoint(4,"52", CheckpointState.STEP_UNDO),
        Checkpoint(5,"70", CheckpointState.STEP_UNDO),
        Checkpoint(6,"81", CheckpointState.STEP_UNDO),
        Checkpoint(7,"91", CheckpointState.STEP_UNDO),
        Checkpoint(8,"Ф", CheckpointState.STEP_UNDO)
    )

    var currentCheckpoint: Checkpoint = checkpoints[0]

    var currentIronPeopleCheckpoint: Checkpoint = checkpoints[0]


}