package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.CheckpointResultPojo
import com.gmail.maystruks08.domain.entities.*

fun List<RunnerTableView>.toRunners(checkpoints: List<Checkpoint>): List<Runner> {
    return this.map { tableView ->
        Runner(
            id = tableView.runnerTable.id,
            number = tableView.runnerTable.number,
            fullName = tableView.runnerTable.fullName,
            city = tableView.runnerTable.city,
            sex = RunnerSex.fromOrdinal(tableView.runnerTable.sex),
            dateOfBirthday = tableView.runnerTable.dateOfBirthday,
            type = RunnerType.fromOrdinal(tableView.runnerTable.type),
            totalResult = tableView.runnerTable.totalResult,
            checkpoints = checkpoints.map { cp ->
                val result = tableView.results.find { it.checkpointId == cp.id }
                if (result != null) {
                    CheckpointResult(
                        id = cp.id,
                        name = cp.name,
                        type = cp.type,
                        date = result.time!!,
                        hasPrevious = result.hasPrevious
                    )
                } else cp
            }.toMutableList(),
            isOffTrack = tableView.runnerTable.isOffTrack
        )
    }
}

fun Runner.toRunnerTable(): RunnerTable {
    return RunnerTable(
        id = this.id,
        number = this.number,
        fullName = this.fullName,
        city = this.city,
        sex = this.sex.ordinal,
        dateOfBirthday = this.dateOfBirthday,
        totalResult = this.totalResult,
        type = this.type.ordinal,
        isOffTrack = this.isOffTrack
    )
}

fun List<Checkpoint>.toCheckpointsResult(runnerId: String): List<ResultTable> =
    this.mapNotNull { if (it is CheckpointResult) it.toResultTable(runnerId) else null }


fun List<Runner>.toFirestoreRunners(): List<RunnerPojo> = this.map { it.toFirestoreRunner() }
fun List<RunnerPojo>.fromFirestoreRunners(): List<Runner> = this.map { it.fromFirestoreRunner()}

fun Runner.toFirestoreRunner(): RunnerPojo {
    return RunnerPojo(
        id, number, fullName, sex.ordinal, city, dateOfBirthday, type.ordinal, totalResult,
        checkpoints.toFirestoreCheckpointsResult(), checkpoints.toFirestoreCheckpoints(),
        isOffTrack
    )
}

fun RunnerPojo.fromFirestoreRunner(): Runner {
    return  Runner(
        id, number, fullName, RunnerSex.fromOrdinal(sex), city, dateOfBirthday, RunnerType.fromOrdinal(type), totalResult,
        mutableListOf<Checkpoint>().apply {
            addAll(completeCheckpoints.fromFirestoreCheckpointsResult())
            addAll(uncompletedCheckpoints.fromFirestoreCheckpoints())
            sortBy { it.id }
        },
        isOffTrack
    )
}

fun List<Checkpoint>.toFirestoreCheckpoints(): List<CheckpointPojo> = this.mapNotNull { if (it !is CheckpointResult) it.toFirestoreCheckpoint() else null }
fun List<Checkpoint>.toFirestoreCheckpointsResult(): List<CheckpointResultPojo> = this.mapNotNull { if (it is CheckpointResult) it.toFirestoreCheckpointResult() else null }

fun List<CheckpointPojo>.fromFirestoreCheckpoints(): List<Checkpoint> = this.map { it.fromFirestoreCheckpoint()}
fun List<CheckpointResultPojo>.fromFirestoreCheckpointsResult(): List<CheckpointResult> = this.map { it.fromFirestoreCheckpointResult()}

fun Checkpoint.toFirestoreCheckpoint() = CheckpointPojo(id, name, type.ordinal)
fun CheckpointResult.toFirestoreCheckpointResult() = CheckpointResultPojo(id, name, type.ordinal, date, hasPrevious)

fun CheckpointPojo.fromFirestoreCheckpoint() = Checkpoint(id, name, CheckpointType.fromOrdinal(type))
fun CheckpointResultPojo.fromFirestoreCheckpointResult() = CheckpointResult(id, name, CheckpointType.fromOrdinal(type), date, hasPrevious)


