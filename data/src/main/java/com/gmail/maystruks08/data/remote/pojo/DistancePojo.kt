package com.gmail.maystruks08.data.remote.pojo

import com.gmail.maystruks08.domain.entities.DistanceType
import java.util.*

data class DistancePojo(
    val id: String = "",
    val raceId: String = "",
    val name: String = "",
    val type: String = DistanceType.MARATHON.name,
    val authorId: String = "",
    val dateOfStart: Date? = null,
    val runnerIds: List<String> = emptyList()
)