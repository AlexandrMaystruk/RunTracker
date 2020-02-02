package com.gmail.maystruks08.domain.entities

import java.util.*

data class Checkpoint(val id: String, var state: CheckpointState,  var date: Date? = null, val checkpointSettings: CheckpointSettings)