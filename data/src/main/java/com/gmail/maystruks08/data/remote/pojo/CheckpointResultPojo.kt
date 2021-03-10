package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class CheckpointResultPojo(
    val id: Long = -1,
    val distanceId: String = "unknown",
    val raceId: String = "unknown",
    val name: String = "unknown",
    val date: Date = Date(),
    val hasPrevious: Boolean = true
)