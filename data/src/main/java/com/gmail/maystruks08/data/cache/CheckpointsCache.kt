package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckpointsCache @Inject constructor() {

    var checkpoints = listOf(
        Checkpoint(0,"С", CheckpointState.CURRENT),
        Checkpoint(1,"15", CheckpointState.UNDONE),
        Checkpoint(2,"46", CheckpointState.UNDONE),
        Checkpoint(3,"52", CheckpointState.UNDONE),
        Checkpoint(4,"70", CheckpointState.UNDONE),
        Checkpoint(5,"81", CheckpointState.UNDONE),
        Checkpoint(6,"90", CheckpointState.UNDONE),
        Checkpoint(7,"Ф", CheckpointState.UNDONE)
    )

    var checkpointsIronPeople = listOf(
        Checkpoint(0,"С", CheckpointState.CURRENT),
        Checkpoint(1,"7.5", CheckpointState.UNDONE),
        Checkpoint(2,"15", CheckpointState.UNDONE),
        Checkpoint(3,"42", CheckpointState.UNDONE),
        Checkpoint(4,"52", CheckpointState.UNDONE),
        Checkpoint(5,"70", CheckpointState.UNDONE),
        Checkpoint(6,"81", CheckpointState.UNDONE),
        Checkpoint(7,"91", CheckpointState.UNDONE),
        Checkpoint(8,"Ф", CheckpointState.UNDONE)
    )

    var currentCheckpoint: Checkpoint = checkpoints[0]

    var currentIronPeopleCheckpoint: Checkpoint = checkpoints[0]

}