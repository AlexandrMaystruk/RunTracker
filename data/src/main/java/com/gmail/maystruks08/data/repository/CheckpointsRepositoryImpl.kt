package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.UserSettingsDAO
import com.gmail.maystruks08.data.local.entity.tables.UserSettingsTable
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.mappers.toFirestoreDistanceCheckpoint
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

    override suspend fun getCheckpoints(distanceId: String): List<Checkpoint> {
        if (networkUtil.isOnline()) {
            val checkpointsDocument = firestoreApi.getCheckpoints(distanceId)
            checkpointsDocument.data?.toDataClass<HashMap<String, CheckpointPojo>?>()
                ?.let { hashMap ->
                    val checkpoints = hashMap.values
                    checkpointDAO.deleteCheckpointsByRaceAndDistanceId(distanceId)
                    checkpointDAO.insertAllOrReplace(checkpoints.map { it.toCheckpointTable() })
                }
        }
        val checkpointsTable = checkpointDAO.getCheckpointsByDistanceId(distanceId)
        return checkpointsTable.map {
            CheckpointImpl(
                it.checkpointId,
                it.distanceId,
                it.name,
                it.position
            )
        }
    }

    override suspend fun getCurrentCheckpoint(raceId: String, distanceId: String): Checkpoint? {
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
        raceId: String,
        distanceId: String,
        checkpointId: String
    ) {
        auth.currentUser?.uid?.let { currentUserId ->
            var currentUserSettings =
                userSettingsDAO.getUserSettings(currentUserId, raceId, distanceId)
            if (currentUserSettings != null) {
                val updatedSettings = currentUserSettings.copy(currentCheckpointId = checkpointId)
                userSettingsDAO.updateUserSettings(updatedSettings)
            } else {
                currentUserSettings = UserSettingsTable(currentUserId, raceId, distanceId, checkpointId)
                userSettingsDAO.insertOrReplace(currentUserSettings)
            }
        }
    }

    override suspend fun saveEditedCheckpoints(distanceId: String, editedCheckpoints: List<Checkpoint>) {
        checkpointDAO.deleteCheckpointsByRaceAndDistanceId(distanceId)
        firestoreApi.deleteDistanceCheckpoints(distanceId)
        val firestoreDistanceCheckpoints  = editedCheckpoints.map { it.toFirestoreDistanceCheckpoint() }
        firestoreApi.saveDistanceCheckpoints(distanceId, firestoreDistanceCheckpoints)
    }
}