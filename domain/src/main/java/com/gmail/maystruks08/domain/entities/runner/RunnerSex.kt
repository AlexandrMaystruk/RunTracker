package com.gmail.maystruks08.domain.entities.runner

enum class RunnerSex {

    MALE, FEMALE;

    companion object {
        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: MALE
    }
}