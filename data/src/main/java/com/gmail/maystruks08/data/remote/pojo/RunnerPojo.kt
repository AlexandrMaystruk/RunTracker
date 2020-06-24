package com.gmail.maystruks08.data.remote.pojo

import java.util.*

class RunnerPojo(
    val id: String = "",
    val number: Int = 0,
    val fullName: String = "",
    val sex: Int = 0,
    val city: String = "",
    val dateOfBirthday: Date = Date(),
    val type: Int = 0,
    var totalResult: Date? = null,
    var completeCheckpoints: List<CheckpointResultPojo> = listOf(),
    val uncompletedCheckpoints: List<CheckpointPojo> = listOf(),
    val isOffTrack: Boolean = false
)
