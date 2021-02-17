package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class RacePojo(
    val id: Long = 0,
    val name: String = "",
    val dateCreation: Date = Date(),
    val registrationIsOpen: Boolean = true,
    val authorId: Long = 0,
    val adminListIds:  List<String> = emptyList(),
    val distanceListIds: List<String> = emptyList()
)