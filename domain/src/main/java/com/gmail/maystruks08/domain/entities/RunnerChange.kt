package com.gmail.maystruks08.domain.entities

data class RunnerChange(val runner: Runner, val changeType: Change)
enum class Change{ UPDATE, ADD, REMOVE }
