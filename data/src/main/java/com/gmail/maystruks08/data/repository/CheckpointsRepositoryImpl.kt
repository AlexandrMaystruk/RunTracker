package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.UserSettingsDAO
import com.gmail.maystruks08.data.local.entity.tables.UserSettingsTable
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.toDataClass
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.inject.Inject


class CheckpointsRepositoryImpl @Inject constructor(
    private val firestoreApi: Api,
    private val checkpointDAO: CheckpointDAO,
    private val userSettingsDAO: UserSettingsDAO,
    private val networkUtil: NetworkUtil,
    private val auth: FirebaseAuth
) : CheckpointsRepository {

    override suspend fun getCheckpoints(raceId: Long, distanceId: Long): List<Checkpoint> {
        if (networkUtil.isOnline()) {
            val checkpointsDocument = firestoreApi.getCheckpoints(raceId.toString(), distanceId.toString())
            checkpointsDocument.data?.toDataClass<HashMap<String, CheckpointPojo>?>()
                ?.let { hashMap ->
                    val checkpoints = hashMap.values
                    checkpointDAO.deleteCheckpointsByRaceAndDistanceId(raceId, distanceId)
                    checkpointDAO.insertAllOrReplace(checkpoints.map { it.toCheckpointTable() })
                }
        }
        val checkpointsTable = checkpointDAO.getCheckpointsByRaceAndDistanceId(raceId, distanceId)
        return checkpointsTable.map {
            CheckpointImpl(
                it.checkpointId,
                it.distanceId,
                it.raceId,
                it.name
            )
        }
    }

    override suspend fun getCurrentCheckpoint(raceId: Long, distanceId: Long): Checkpoint? {
        return auth.currentUser?.uid?.let { currentUserId ->
            userSettingsDAO.getUserSettings(currentUserId, raceId, distanceId)
                ?.let { currentUserSettings ->
                    currentUserSettings.currentCheckpointId?.let { checkpointId ->
                        checkpointDAO.getCheckpoint(distanceId, checkpointId)?.toCheckpoint()
                    }
                }

        }
    }

    override suspend fun saveCurrentSelectedCheckpointId(
        raceId: Long,
        distanceId: Long,
        checkpointId: Long
    ) {
        auth.currentUser?.uid?.let { currentUserId ->
            var currentUserSettings =
                userSettingsDAO.getUserSettings(currentUserId, raceId, distanceId)
            if (currentUserSettings != null) {
                val updatedSettings = currentUserSettings.copy(currentCheckpointId = checkpointId)
                userSettingsDAO.updateUserSettings(updatedSettings)
            } else {
                currentUserSettings =
                    UserSettingsTable(currentUserId, raceId, distanceId, checkpointId)
                userSettingsDAO.insertOrReplace(currentUserSettings)
            }
        }
    }
}