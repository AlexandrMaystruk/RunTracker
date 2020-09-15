package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class RunnerPojo(
    var number: Int = 0,
    var cardId: String = "",
    var fullName: String = "",
    val shortName: String = "",
    val phone: String = "",
    var sex: Int = 0,
    var city: String = "",
    var dateOfBirthday: Date = Date(),
    var type: Int = 0,
    val teamName: String? = null,
    var totalResult: Date? = null,
    var completeCheckpoints: List<CheckpointResultPojo> = listOf(),
    var uncompletedCheckpoints: List<CheckpointPojo> = listOf(),
    var isOffTrack: Boolean = false
)
