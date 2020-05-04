package com.gmail.maystruks08.domain.entities

//TODO val startWorkingTime: Date? = null,
//TODO val finishWorkingTime: Date? = null
open class Checkpoint(open val id: Int = -1, open val name: String = "unknown", open val type: CheckpointType)