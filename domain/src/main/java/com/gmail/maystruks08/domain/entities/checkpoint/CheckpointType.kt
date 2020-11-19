package com.gmail.maystruks08.domain.entities.checkpoint

enum class CheckpointType {

    NORMAL, IRON;

    companion object {
        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: NORMAL
    }
}