package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable
import com.gmail.maystruks08.data.local.pojo.RunnerTableView
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.CheckpointResultPojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex

fun List<RunnerTableView>.toRunners(checkpoints: List<CheckpointImpl>): MutableList<Runner> {
    return ArrayList<Runner>().apply {
        this@toRunners.forEach { add(it.toRunner(checkpoints)) }
    }
}

fun RunnerTableView.toRunner(checkpoints: List<CheckpointImpl>): Runner {
    return Runner(
        cardId = runnerTable.cardId,
        number = runnerTable.runnerNumber,
        fullName = runnerTable.fullName,
        shortName = runnerTable.shortName,
        phone = runnerTable.phone,
        city = runnerTable.city,
        sex = RunnerSex.fromOrdinal(runnerTable.sex),
        dateOfBirthday = runnerTable.dateOfBirthday,
        teamName = runnerTable.teamName,
        totalResult = runnerTable.totalResult,
//        checkpoints = checkpoints.map { cp ->
//            val result = results.find { it.checkpointId == cp.id }
//            if (result != null) {
//                CheckpointResultIml(
//                    id = cp.id,
//                    name = cp.name,
//                    type = cp.type,
//                    date = result.time!!,
//                    hasPrevious = result.hasPrevious
//                )
//            } else cp
//        }.sortedBy { it.id }.toMutableList(),

        checkpoints = mutableListOf(),
        isOffTrack = runnerTable.isOffTrack,
        distanceIds = mutableListOf(),
        actualDistanceId = 0
    )
}


fun Runner.toRunnerTable(needToSync: Boolean = true): RunnerTable {
    return RunnerTable(
        cardId = this.cardId,
        runnerNumber = this.number,
        fullName = this.fullName,
        shortName = this.shortName,
        phone = this.phone,
        city = this.city,
        sex = this.sex.ordinal,
        dateOfBirthday = this.dateOfBirthday,
        teamName = this.teamName,
        totalResult = this.totalResult,
        isOffTrack = this.isOffTrack,
        needToSync = needToSync
    )
}

fun List<CheckpointImpl>.toCheckpointsResult(runnerNumber: Int): List<ResultTable> =
//    this.mapNotNull { if (it is CheckpointResultIml) it.toResultTable(runnerNumber) else null }
    mutableListOf()

fun Runner.toFirestoreRunner(): RunnerPojo {
    return RunnerPojo(
        number,
        cardId,
        fullName,
        shortName,
        phone,
        sex.ordinal,
        city,
        dateOfBirthday,
       0,
        teamName,
        totalResult,
        mutableListOf(),
        mutableListOf(),
        isOffTrack
    )
}

fun RunnerPojo.fromFirestoreRunner(): Runner {
    return Runner(
        number.toLong(),
        cardId,
        fullName,
        shortName,
        phone,
        RunnerSex.fromOrdinal(sex),
        city,
        dateOfBirthday,
        0,
        listOf(),
        mutableListOf(),
        isOffTrack,
        teamName,
        totalResult
    )
}

fun List<CheckpointImpl>.toFirestoreCheckpoints(): List<CheckpointPojo> = emptyList()
//    this.mapNotNull { if (it !is CheckpointResultIml) it.toFirestoreCheckpoint() else null }

fun List<CheckpointImpl>.toFirestoreCheckpointsResult(): List<CheckpointResultPojo> = emptyList()
//    this.mapNotNull { if (it is CheckpointResultIml) it.toFirestoreCheckpointResult() else null }

fun List<CheckpointPojo>.fromFirestoreCheckpoints(): List<CheckpointImpl> =
    this.map { it.fromFirestoreCheckpoint() }

fun List<CheckpointResultPojo>.fromFirestoreCheckpointsResult(): List<CheckpointResultIml> =
    this.map { it.fromFirestoreCheckpointResult() }

fun Checkpoint.toFirestoreCheckpoint() = CheckpointPojo(getId(), getName(), getDistanceId())
fun Checkpoint.toFirestoreCheckpointResult() =
    CheckpointResultPojo(getId(), getName(), getDistanceId(), getResult()!!, hasPrevious())

fun CheckpointPojo.fromFirestoreCheckpoint() = CheckpointImpl(id, name, distanceId)

fun CheckpointResultPojo.fromFirestoreCheckpointResult() = CheckpointResultIml(CheckpointImpl(id, name, distanceId), date, hasPrevious)


