package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint

interface CheckpointsRepository {

    suspend fun getCheckpoints(raceId: Long, distanceId: Long): List<Checkpoint>

    suspend fun getCurrentCheckpoint(raceId: Long, distanceId: Long): Checkpoint?

    suspend fun saveCurrentSelectedCheckpointId(
        raceId: Long,
        distanceId: Long,
        checkpointId: Long
    )

}