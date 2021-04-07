package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.parseServerTime
import java.util.*


fun RunnerPojo.fromFirestoreRunner(): Runner {
    val checkpointsResult = mutableMapOf<String, MutableList<Checkpoint>>()
    checkpoints.forEach { (t, u) ->
        checkpointsResult[t] = u.map {
            CheckpointResultIml(
                CheckpointImpl(it.id, it.distanceId, it.name),
                it.runnerTime.parseServerTime()
            )
        }.toMutableList()
    }
    val totalResultsMap = mutableMapOf<String, Date?>().apply {
        totalResults.forEach { (t, u) -> put(t, u?.parseServerTime()) }
    }
    return Runner(
        number,
        cardId,
        fullName,
        shortName,
        phone,
        RunnerSex.fromOrdinal(sex),
        city,
        dateOfBirthday.parseServerTime(),
        actualRaceId,
        actualDistanceId,
        raceIds,
        distanceIds,
        checkpointsResult,
        offTrackDistances,
        teamNames,
        totalResultsMap
    )
}

fun Checkpoint.toFirestoreCheckpoint() = CheckpointPojo(getId(), getDistanceId(), getName())


