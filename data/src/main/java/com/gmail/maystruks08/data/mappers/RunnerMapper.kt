package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.domain.entities.*

fun List<RunnerTableView>.toRunners(): List<Runner> {
    return this.groupBy { it.id }.map { entry ->
        val runner = entry.value.first()
         Runner(
            id = runner.id,
            number = runner.number,
            fullName = runner.fullName,
            city = runner.city,
            dateOfBirthday = runner.dateOfBirthday,
            type = RunnerType.fromOrdinal(runner.type),
            totalResult = runner.totalResult,
            checkpoints = entry.value.mapNotNull {
                val date = it.time ?: return@mapNotNull null
                CheckpointResult(
                id = it.checkpointId,
                name = it.name,
                type = CheckpointType.fromOrdinal(it.type),
                date = date,
                hasPrevious = it.hasPrevious
            ) }.toMutableList()
        )
    }
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