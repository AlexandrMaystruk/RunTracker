package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.fromJson
import com.gmail.maystruks08.data.fromJsonOrNull
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithRunners
import com.gmail.maystruks08.data.local.entity.relation.RaceWithDistances
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable
import com.gmail.maystruks08.data.local.entity.tables.RaceTable
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.google.gson.Gson
import java.util.*

/**
 * Convert database table to entity
 */
fun RaceWithDistances.toRaceEntity(gson: Gson): Race {
    val distances = distancesWithRunners.map { it.toDistanceEntity(gson) }.toMutableList()
    return Race(
        id = raceTable.id,
        name = raceTable.name,
        dateCreation = Date(raceTable.dateCreation),
        registrationIsOpen = raceTable.registrationIsOpen,
        authorId = raceTable.authorId,
        distanceList = distances,
        adminListIds = gson.fromJsonOrNull(raceTable.adminListIds) ?: mutableListOf()
    )
}

fun DistanceWithRunners.toDistanceEntity(gson: Gson): Distance {
    val checkpointList = checkpoints.map { it.toCheckpoint() }.toMutableList()
    val runners = runners.map { it.toRunner(gson) }
        .toSortedSet(compareBy<Runner> { it.totalResult }
            .thenBy { it.isOffTrack }
            .thenBy { runner -> runner.checkpoints.count { it.getResult() != null } })
    return Distance(
        id = distance.distanceId,
        name = distance.name,
        authorId = distance.authorId,
        dateOfStart = Date(distance.dateOfStart),
        checkpoints = checkpointList,
        runners = runners
    )
}

fun RunnerTable.toRunner(gson: Gson): Runner {
    return Runner(
        cardId = cardId,
        number = runnerNumber,
        fullName = fullName,
        shortName = shortName,
        phone = phone,
        city = city,
        sex = RunnerSex.fromOrdinal(sex),
        dateOfBirthday = dateOfBirthday,
        teamName = teamName,
        totalResult = totalResult,
        checkpoints = mutableListOf(),
        isOffTrack = isOffTrack,
        distanceIds = gson.fromJson(distanceIds),
        raceIds = gson.fromJson(raceIds),
        actualDistanceId = 0
    )
}

fun CheckpointTable.toCheckpoint(): Checkpoint {
    return CheckpointImpl(
        this.checkpointId.toLong(),
        this.name,
        distanceId.toLong()
    )
}


/**
 * Convert entity to database table
 */

fun Race.toRaceTable(gson: Gson): RaceTable {
    return RaceTable(
        id,
        name,
        dateCreation.time,
        authorId,
        registrationIsOpen,
        gson.toJson(adminListIds),
        gson.toJson(distanceList.map { it.id })
    )
}


fun Runner.toRunnerTable(gson: Gson, needToSync: Boolean = true): RunnerTable {
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
        distanceIds = gson.toJson(this.distanceIds),
        raceIds = gson.toJson(this.raceIds),
        needToSync = needToSync
    )
}

fun Checkpoint.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        getId().toInt(),
        getDistanceId().toInt(),
        getName(),
    )
}

fun CheckpointResultIml.toResultTable(runnerNumber: Int): ResultTable {
    return ResultTable(
        resultId = 0,
        checkpointId = 0,
        time = this.getResult(),
        hasPrevious = this.hasPrevious()
    )
}




/**
 * Convert entity to firestore pojo
 */

fun Race.toFirestoreRace(gson: Gson): RacePojo {
    return RacePojo(
        id,
        name,
        dateCreation,
        registrationIsOpen,
        authorId,
        adminListIds,
        distanceList.map { it.id.toString() }
    )
}


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
        actualDistanceId,
        teamName,
        totalResult,
        distanceIds,
        raceIds,
        mutableListOf(), //TODO
        mutableListOf(),//TODO
        isOffTrack
    )
}

/**
 * Convert firestore pojo to table
 */

fun RacePojo.toTable(gson: Gson): RaceTable {
    return RaceTable(
        id = id,
        name = name,
        dateCreation = dateCreation.time,
        authorId = authorId,
        registrationIsOpen = registrationIsOpen,
        adminListIds = gson.toJson(adminListIds),
        distanceListIds = gson.toJson(distanceListIds)
    )
}

fun CheckpointPojo.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        this.id.toInt(),
        this.distanceId.toInt(),
        this.name,
    )
}