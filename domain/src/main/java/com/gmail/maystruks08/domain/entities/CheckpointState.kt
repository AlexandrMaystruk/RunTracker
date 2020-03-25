package com.gmail.maystruks08.domain.entities

enum class CheckpointState(val id: Int) {
    UNDONE(-1),
    CURRENT(0),
    DONE(1);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id } ?: UNDONE
    }
}