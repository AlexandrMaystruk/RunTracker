package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import javax.inject.Inject


class CheckpointsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val checkpointDAO: CheckpointDAO,
    private val settingsCache: SettingsCache,
    private val networkUtil: NetworkUtil
) : CheckpointsRepository {

    override suspend fun getCheckpoints(distanceId: Long): List<CheckpointImpl> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentCheckpoint(distanceId: Long): CheckpointImpl {
        TODO("Not yet implemented")
    }

    override suspend fun saveCurrentSelectedCheckpoint(checkpoint: CheckpointImpl) {
        TODO("Not yet implemented")
    }

}