package com.gmail.maystruks08.domain.entities

enum class RunnerType(val id: Int) {
    NORMAL(0), IRON(1);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id } ?: NORMAL
    }
}