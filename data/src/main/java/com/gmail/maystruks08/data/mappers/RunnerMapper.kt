package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.CheckpointResultPojo
import com.gmail.maystruks08.domain.entities.*

fun List<RunnerTableView>.toRunners(checkpoints: List<Checkpoint>): List<Runner> {
    return this.map { it.toRunner(checkpoints) }
}

fun RunnerTableView.toRunner(checkpoints: List<Checkpoint>): Runner {
    return Runner(
            id = runnerTable.id,
            number = runnerTable.number,
            fullName = runnerTable.fullName,
            shortName = runnerTable.shortName,
            phone = runnerTable.phone,
            city = runnerTable.city,
            sex = RunnerSex.fromOrdinal(runnerTable.sex),
            dateOfBirthday = runnerTable.dateOfBirthday,
            type = RunnerType.fromOrdinal(runnerTable.type),
            teamName = runnerTable.teamName,
            totalResult = runnerTable.totalResult,
            checkpoints = checkpoints.map { cp ->
                val result = results.find { it.checkpointId == cp.id }
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
            isOffTrack = runnerTable.isOffTrack
        )
}

fun Runner.toRunnerTable(needToSync: Boolean = true): RunnerTable {
    return RunnerTable(
        id = this.id,
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

fun List<Checkpoint>.toCheckpointsResult(runnerId: String): List<ResultTable> =
    this.mapNotNull { if (it is CheckpointResult) it.toResultTable(runnerId) else null }


fun List<Runner>.toFirestoreRunners(): List<RunnerPojo> = this.map { it.toFirestoreRunner() }
fun List<RunnerPojo>.fromFirestoreRunners(): List<Runner> = this.map { it.fromFirestoreRunner()}

fun Runner.toFirestoreRunner(): RunnerPojo {
    return RunnerPojo(
        id, number, fullName, shortName, phone, sex.ordinal, city, dateOfBirthday, type.ordinal, teamName, totalResult,
        checkpoints.toFirestoreCheckpointsResult(), checkpoints.toFirestoreCheckpoints(),
        isOffTrack
    )
}

fun RunnerPojo.fromFirestoreRunner(): Runner {
    return  Runner(
        id, number, fullName, shortName, phone,  RunnerSex.fromOrdinal(sex), city, dateOfBirthday, RunnerType.fromOrdinal(type), totalResult, teamName,
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


