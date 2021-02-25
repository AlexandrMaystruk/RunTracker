package com.gmail.maystruks08.domain.entities.checkpoint

import java.util.*

interface Checkpoint {

    fun getId(): Long

    fun getName(): String

    fun getDistanceId(): Long

    fun getRaceId(): Long

    fun getResult(): Date?

    fun hasPrevious(): Boolean

    fun setHasPrevious(flag: Boolean)

}



