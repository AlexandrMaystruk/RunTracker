package com.gmail.maystruks08.domain.entities

import java.util.*

data class Checkpoint(val id: String = "unknown", var state: CheckpointState = CheckpointState.STEP_UNDO, var date: Date? = null)