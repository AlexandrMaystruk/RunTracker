package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class DistancePojo(
    val id: Long = 0,
    val raceId: Long = 0,
    val name: String = "",
    val authorId: Long = 0,
    val dateOfStart: Date = Date(),
    val checkpointsIds: List<Long> = emptyList(),
    val runnerIds: List<Long> = emptyList()
)