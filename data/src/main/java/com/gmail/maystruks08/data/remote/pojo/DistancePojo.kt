package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class DistancePojo(
    val id: String = "",
    val raceId: String = "",
    val name: String = "",
    val authorId: String = "",
    val dateOfStart: Date = Date(),
    val checkpointsIds: List<Long> = emptyList(),
    val runnerIds: List<Long> = emptyList()
)