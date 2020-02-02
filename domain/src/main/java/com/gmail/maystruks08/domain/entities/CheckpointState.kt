package com.gmail.maystruks08.domain.entities

enum class CheckpointState(val id: Int) {
    STEP_UNDO(-1),
    STEP_CURRENT(0),
    STEP_COMPLETED(1);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id } ?: STEP_UNDO
    }
}