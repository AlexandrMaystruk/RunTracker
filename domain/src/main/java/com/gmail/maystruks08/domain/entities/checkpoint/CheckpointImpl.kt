package com.gmail.maystruks08.domain.entities.checkpoint

import java.util.*

data class CheckpointImpl(private val _id: Long,
                          private val _name: String,
                          private val _distanceId: Long
) : Checkpoint {

    override fun getId(): Long {
        return _id
    }

    override fun getName(): String {
        return _name
    }

    override fun getDistanceId(): Long {
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