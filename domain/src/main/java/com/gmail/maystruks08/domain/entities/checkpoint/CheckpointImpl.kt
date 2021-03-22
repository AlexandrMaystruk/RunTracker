package com.gmail.maystruks08.domain.entities.checkpoint

import java.util.*

data class CheckpointImpl(private val _id: String,
                          private val _distanceId: String,
                          private val _name: String,
) : Checkpoint {

    override fun getId(): String {
        return _id
    }

    override fun getName(): String {
        return _name
    }

    override fun getDistanceId(): String {
        return _distanceId
    }

    override fun getResult(): Date? {
        return null
    }

    override fun hasPrevious(): Boolean {
        return false
    }

    override fun setHasPrevious(flag: Boolean) {}
}