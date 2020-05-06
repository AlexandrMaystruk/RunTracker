package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class CheckpointResultPojo(
    val id: Int = -1,
    val name: String = "",
    val type: Int = 0,
    val date: Date = Date(),
    val hasPrevious: Boolean = true
)