package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex


fun RunnerPojo.fromFirestoreRunner(): Runner {
    return Runner(
        number,
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

fun Checkpoint.toFirestoreCheckpoint() = CheckpointPojo(getId(), getName(), getDistanceId())


