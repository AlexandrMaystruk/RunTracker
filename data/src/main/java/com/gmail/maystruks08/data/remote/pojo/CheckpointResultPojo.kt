package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class CheckpointResultPojo(
    val id: Long = -1,
    val name: String = "",
    val distanceId: Long = 0,
    val date: Date = Date(),
    val hasPrevious: Boolean = true
)