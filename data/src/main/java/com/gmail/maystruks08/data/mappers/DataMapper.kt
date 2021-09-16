package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.fromJsonOrNull
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithRunners
import com.gmail.maystruks08.data.local.entity.relation.RaceWithDistances
import com.gmail.maystruks08.data.local.entity.tables.*
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceStatistic
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.toServerFormat
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
    val runners = runners?.map { it.toRunner(gson) }
        ?.toSortedSet(compareBy<Runner> { it.totalResults[it.actualDistanceId] }
            .thenBy {runner -> runner.offTrackDistances.any { it == runner.actualDistanceId } }
            .thenBy {runner -> runner.checkpoints[runner.actualDistanceId]?.count { it.getResult() != null } })
    val statistic = DistanceStatistic(distance.distanceId, distance.runnerCountInProgress, distance.runnerCountOffTrack, distance.finisherCount)
    return Distance(
        id = distance.distanceId,
        raceId = distance.raceId,
        name = distance.name,
        type = DistanceType.valueOf(distance.type),
        authorId = distance.authorId,
        dateOfStart = distance.dateOfStart?.let { Date(it) },
        checkpoints = checkpointList,
        statistic = statistic,
        runners = runners ?: mutableSetOf()
    )
}

fun DistanceTable.toDistanceEntity(checkpoints: MutableList<Checkpoint>): Distance {
    return Distance(
        id = distanceId,
        raceId = raceId,
        name = name,
        type = DistanceType.valueOf(type),
        authorId = authorId,
        dateOfStart = dateOfStart?.let { Date(it) },
        checkpoints = checkpoints,
        statistic = DistanceStatistic(
            distanceId,
            runnerCountInProgress,
            runnerCountOffTrack,
            finisherCount
        ),
        runners = mutableSetOf()
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
        teamNames = mutableMapOf(),
        totalResults = mutableMapOf(),
        checkpoints = mutableMapOf(),
        offTrackDistances = gson.fromJsonOrNull(isOffTrackMapJson)?: mutableListOf(),
        distanceIds = mutableListOf(),
        raceIds = mutableListOf(),
        actualDistanceId = actualDistanceId,
        actualRaceId = actualRaceId
    )
}


fun CheckpointTable.toCheckpoint(): Checkpoint {
    return CheckpointImpl(
        _id = this.checkpointId,
        _distanceId = this.distanceId,
        _name = this.name,
        _position = this.position
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
        actualDistanceId = this.actualDistanceId,
        actualRaceId =  this.actualRaceId,
        isOffTrackMapJson = gson.toJson(this.offTrackDistances),
        needToSync = needToSync
    )
}

fun Checkpoint.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        checkpointId = getId(),
        distanceId = getDistanceId(),
        name = getName(),
        position = getPosition()
    )
}

fun CheckpointResultIml.toResultTable(runnerNumber: String): ResultTable {
    return ResultTable(
        runnerNumber = runnerNumber,
        checkpointId = getId(),
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
        distanceList.map { it.id }
    )
}


fun Distance.toFirestoreDistance(raceId: String): DistancePojo {
    return DistancePojo(
        id = id,
        raceId = raceId,
        name = name,
        authorId = authorId,
        dateOfStart = dateOfStart,
        runnerIds = runners.map { it.number }
    )
}


fun Runner.toFirestoreRunner(): RunnerPojo {
    val checkpointsResult = mutableMapOf<String, List<CheckpointPojo>>().apply {
        checkpoints.forEach { (distanceId, checkpoints) ->
            this[distanceId] = checkpoints.mapNotNull {
                if (it is CheckpointResultIml) CheckpointPojo(
                    it.getId(),
                    it.getDistanceId(),
                    it.getName(),
                    it.getResult().toServerFormat()
                ) else null
            }
        }
    }
    val totalResultsMap = mutableMapOf<String, String?>().apply {
        totalResults.forEach { (t, u) -> put(t, u?.toServerFormat()) }
    }
    return RunnerPojo(
        number = number,
        cardId = cardId,
        fullName = fullName,
        shortName = shortName,
        phone = phone,
        sex = sex.ordinal,
        city = city,
        dateOfBirthday = dateOfBirthday?.toServerFormat(),
        actualRaceId = actualRaceId,
        actualDistanceId = actualDistanceId,
        raceIds = raceIds,
        distanceIds = distanceIds,
        checkpoints = checkpointsResult,
        offTrackDistances = offTrackDistances,
        teamNames = teamNames,
        totalResults = totalResultsMap
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

fun DistancePojo.toTable(): Pair<DistanceTable, List<DistanceRunnerCrossRef>> {
    return DistanceTable(
        distanceId = id,
        raceId = raceId,
        name = name,
        type = type,
        authorId = authorId,
        dateOfStart = dateOfStart?.time
    ) to runnerIds.map { DistanceRunnerCrossRef(id, it) }
}

fun CheckpointPojo.toCheckpointTable(): CheckpointTable {
    return CheckpointTable(
        checkpointId = this.id,
        distanceId = this.distanceId,
        name = this.name,
        position = this.position
    )
}