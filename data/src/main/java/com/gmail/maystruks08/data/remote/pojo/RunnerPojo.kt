package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class RunnerPojo(
    var id: String = "",
    var number: Int = 0,
    var fullName: String = "",
    var sex: Int = 0,
    var city: String = "",
    var dateOfBirthday: Date = Date(),
    var type: Int = 0,
    var totalResult: Date? = null,
    var completeCheckpoints: List<CheckpointResultPojo> = listOf(),
    var uncompletedCheckpoints: List<CheckpointPojo> = listOf(),
    var isOffTrack: Boolean = false
)
