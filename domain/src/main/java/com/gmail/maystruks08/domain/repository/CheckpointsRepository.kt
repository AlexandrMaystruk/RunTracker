package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointsRepository {

    suspend fun getCheckpoints(raceId: String, distanceId: String): List<Checkpoint>

    suspend fun getCurrentCheckpoint(raceId: String, distanceId: String): Checkpoint?

    suspend fun saveCurrentSelectedCheckpointId(
        raceId: String,
        distanceId: String,
        checkpointId: Long
    )

}