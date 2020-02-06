package com.gmail.maystruks08.domain.entities

import java.util.*

data class Checkpoint(val id: Int = -1, val name: String = "unknown", var state: CheckpointState = CheckpointState.STEP_UNDO, var date: Date? = null){

    constructor() : this(-1, "unknown", CheckpointState.STEP_UNDO, null)
}