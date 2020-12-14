package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import java.util.*

data class Race(
    val id: Long,
    val name: String,
    val date: Date,
    val registrationIsOpen: Boolean,
    val distanceList: MutableList<Distance>
) {

    fun createNewDistance(
        id: Long,
        name: String,
        authorId: Long,
        checkpoints: MutableList<Checkpoint>? = null,
    ) {
        val newDistance = Distance(id, name, authorId, checkpoints ?: mutableListOf(), mutableListOf())
        distanceList.add(newDistance)
    }

}
