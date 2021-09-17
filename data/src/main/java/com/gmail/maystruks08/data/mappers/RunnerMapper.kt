package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistanceCheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.parseServerTime
import com.gmail.maystruks08.domain.toServerFormat
import java.util.*


fun RunnerPojo.fromFirestoreRunner(): Runner {
    val checkpointsResult = mutableMapOf<String, MutableList<Checkpoint>>()
    checkpoints.forEach { (t, u) ->
        checkpointsResult[t] = u.mapIndexed { index, checkpointPojo ->
            CheckpointResultIml(
                CheckpointImpl(checkpointPojo.id, checkpointPojo.distanceId, checkpointPojo.name, index),
                checkpointPojo.runnerTime.parseServerTime()
            )
        }.toMutableList()
    }
    return Runner(
        number,
        cardId,
        fullName,
        shortName,
        phone,
        RunnerSex.fromOrdinal(sex),
        city,
        dateOfBirthday?.parseServerTime(),
        actualRaceId,
        actualDistanceId,
        raceIds,
        distanceIds,
        checkpointsResult,
        offTrackDistances,
        teamNames,
        mutableMapOf()
    ).also {
        it.calculateTotalResults()
    }
}

fun Checkpoint.toFirestoreCheckpoint() = CheckpointPojo(id = getId(), distanceId = getDistanceId(), name = getName(), runnerTime = getResult()?.toServerFormat().orEmpty(), position = getPosition())

fun Checkpoint.toFirestoreDistanceCheckpoint() = DistanceCheckpointPojo(getId(), getDistanceId(), getName(), getPosition())

