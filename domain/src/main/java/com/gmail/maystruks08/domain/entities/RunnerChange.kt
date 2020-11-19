package com.gmail.maystruks08.domain.entities

import com.gmail.maystruks08.domain.entities.runner.Runner

data class RunnerChange(val runner: Runner, val changeType: Change)
enum class Change{ UPDATE, ADD, REMOVE }
