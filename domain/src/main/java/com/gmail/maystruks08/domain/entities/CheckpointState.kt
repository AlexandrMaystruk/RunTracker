package com.gmail.maystruks08.domain.entities

enum class CheckpointState {

    UNDONE, DONE, CURRENT;

    companion object {

        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: UNDONE
    }
}