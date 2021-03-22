package com.gmail.maystruks08.domain.entities.checkpoint

import java.util.*

data class CheckpointResultIml(
    private val checkpoint: CheckpointImpl,
    private var date: Date,
    private var hasPrevious: Boolean = true
) : Checkpoint {

    override fun getId(): String {
        return checkpoint.getId()
    }

    override fun getName(): String {
        return checkpoint.getName()
    }

    override fun getDistanceId(): String {
        return checkpoint.getDistanceId()
    }

    override fun getResult(): Date {
        return date
    }

    override fun hasPrevious(): Boolean {
        return hasPrevious
    }

    override fun setHasPrevious(flag: Boolean) {
        this.hasPrevious = flag
    }
}