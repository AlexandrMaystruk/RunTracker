package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl

interface CheckpointsRepository {

    suspend fun getCheckpoints(distanceId: Long): List<CheckpointImpl>

    suspend fun getCurrentCheckpoint(distanceId: Long): CheckpointImpl

    suspend fun saveCurrentSelectedCheckpoint(checkpoint: CheckpointImpl)

}