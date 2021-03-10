package com.gmail.maystruks08.data.remote.pojo

import java.util.*

data class RacePojo(
    val id: String = "",
    val name: String = "",
    val dateCreation: Date = Date(),
    val registrationIsOpen: Boolean = true,
    val authorId: String = "",
    val adminListIds:  List<String> = emptyList(),
    val distanceListIds: List<String> = emptyList()
)