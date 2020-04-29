package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.local.pojo.ResultTableView
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.domain.entities.*

fun RunnerTableView.toRunner(): Runner {
    return Runner(
        id = this.runnerTable.id,
        number = this.runnerTable.number,
        fullName = this.runnerTable.fullName,
        city = this.runnerTable.city,
        dateOfBirthday = this.runnerTable.dateOfBirthday,
        type = RunnerType.fromOrdinal(this.runnerTable.type),
        totalResult = this.runnerTable.totalResult,
        checkpoints = this.checkpointsResultTable.mapNotNull {
            it.resultTable.time ?: return@mapNotNull null
            CheckpointResult(it.checkpointTable.id, it.checkpointTable.name, it.resultTable.time, hasPrevious = it.resultTable.hasPrevious)
        }.toMutableList()
    )
}

fun Runner.toRunnerTable(): RunnerTable {
    return RunnerTable(
        id = this.id,
        number = this.number,
        fullName = this.fullName,
        city = this.city,
        dateOfBirthday = this.dateOfBirthday,
        totalResult = this.totalResult,
        type = this.type.ordinal
    )
}