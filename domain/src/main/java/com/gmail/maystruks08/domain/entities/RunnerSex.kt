package com.gmail.maystruks08.domain.entities

enum class RunnerSex {

    MALE, FEMALE;

    companion object {
        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: MALE
    }
}