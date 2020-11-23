package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.CheckpointResultPojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointType
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.entities.runner.RunnerType

fun List<RunnerTableView>.toRunners(checkpoints: List<Checkpoint>): MutableList<Runner> {
    return ArrayList<Runner>().apply {
        this@toRunners.forEach { tableView ->
            add(Runner(
                cardId = tableView.runnerTable.cardId,
                number = tableView.runnerTable.number,
                fullName = tableView.runnerTable.fullName,
                shortName = tableView.runnerTable.shortName,
                phone = tableView.runnerTable.phone,
                city = tableView.runnerTable.city,
                sex = RunnerSex.fromOrdinal(tableView.runnerTable.sex),
                dateOfBirthday = tableView.runnerTable.dateOfBirthday,
                type = RunnerType.fromOrdinal(tableView.runnerTable.type),
                teamName = tableView.runnerTable.teamName,
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
                }.sortedBy { it.id }.toMutableList(),
                isOffTrack = tableView.runnerTable.isOffTrack
            )
            )
        }
    }
}

fun Runner.toRunnerTable(needToSync: Boolean = true): RunnerTable {
    return RunnerTable(
        cardId = this.cardId,
        number = this.number,
        fullName = this.fullName,
        shortName = this.shortName,
        phone = this.phone,
        city = this.city,
        sex = this.sex.ordinal,
        dateOfBirthday = this.dateOfBirthday,
        teamName = this.teamName,
        totalResult = this.totalResult,
        type = this.type.ordinal,
        isOffTrack = this.isOffTrack,
        needToSync = needToSync
    )
}

fun List<Checkpoint>.toCheckpointsResult(runnerNumber: Int): List<ResultTable> =
    this.mapNotNull { if (it is CheckpointResult) it.toResultTable(runnerNumber) else null }

fun Runner.toFirestoreRunner(): RunnerPojo {
    return RunnerPojo(number,cardId, fullName, shortName, phone, sex.ordinal, city, dateOfBirthday, type.ordinal, teamName, totalResult,
        checkpoints.toFirestoreCheckpointsResult(), checkpoints.toFirestoreCheckpoints(),
        isOffTrack
    )
}

fun RunnerPojo.fromFirestoreRunner(): Runner {
    return  Runner(
        number, cardId, fullName, shortName, phone,  RunnerSex.fromOrdinal(sex), city, dateOfBirthday, RunnerType.fromOrdinal(type), totalResult, teamName,
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


