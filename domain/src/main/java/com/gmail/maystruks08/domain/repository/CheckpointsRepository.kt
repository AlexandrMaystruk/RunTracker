package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointsRepository {

    suspend fun getCheckpoints(distanceId: String): List<Checkpoint>

    suspend fun getCurrentCheckpoint(raceId: String, distanceId: String): Checkpoint?

    suspend fun saveCurrentSelectedCheckpointId(
        raceId: String,
        distanceId: String,
        checkpointId: String
    )

}