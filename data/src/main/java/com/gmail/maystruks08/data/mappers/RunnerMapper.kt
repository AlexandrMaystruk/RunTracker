package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.RunnerTable
import com.gmail.maystruks08.data.local.RunnerWithCheckpoints
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType

fun RunnerWithCheckpoints.toRunner(): Runner {
    return Runner(
        id = this.runnerTable.id,
        number = this.runnerTable.number,
        name = this.runnerTable.name,
        surname = this.runnerTable.surname,
        city = this.runnerTable.city,
        dateOfBirthday = this.runnerTable.dateOfBirthday,
        type = RunnerType.fromId(this.runnerTable.type),
        checkpoints = this.checkpointsTable.map { it.toCheckpoint() }
    )
}

fun Runner.toRunnerTable(): RunnerTable {
    return RunnerTable(
        id = this.id,
        number = this.number,
        name = this.name,
        surname = this.surname,
        city = this.city,
        dateOfBirthday = this.dateOfBirthday,
        type = this.type.id
    )
}