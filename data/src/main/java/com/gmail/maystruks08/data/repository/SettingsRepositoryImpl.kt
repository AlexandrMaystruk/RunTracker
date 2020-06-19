package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.awaitTaskResult
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.toDataClass
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointType
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val preferences: ConfigPreferences,
    private val settingsCache: SettingsCache,
    private val firebaseAuth: FirebaseAuth,
    private val checkpointDAO: CheckpointDAO,
    private val networkUtil: NetworkUtil
) : SettingsRepository {

    override suspend fun updateConfig(): ResultOfTask<Exception, SettingsRepository.Config> {
        return ResultOfTask.build {
            if ( networkUtil.isOnline()) {
                val checkpointsSnapshot = awaitTaskResult(firestoreApi.getCheckpoints())
                checkpointsSnapshot.data?.toDataClass<HashMap<String, Checkpoint>?>()?.let { hashMap ->
                        val checkpoints = hashMap.values
                        checkpointDAO.deleteCheckpoints()
                        checkpointDAO.insertAllOrReplace(checkpoints.map { it.toCheckpointTable() })
                    }
                firebaseAuth.currentUser?.uid?.let { currentUserId ->
                    val result = awaitTaskResult(firestoreApi.getCheckpointsSettings(currentUserId))
                    val startDateResult = awaitTaskResult(firestoreApi.getDateOfStart())

                    val checkpointId = result["checkpointId"] as? HashMap<*, *>
                    val checkpointIronPeopleId = result["checkpointIronPeopleId"] as? HashMap<*, *>
                    val startDate = (startDateResult["Start at"] as? com.google.firebase.Timestamp)?.toDate()

                    if (checkpointId != null) preferences.saveCurrentCheckpointId((checkpointId.values.first() as Long).toInt())
                    if (checkpointIronPeopleId != null) preferences.saveCurrentIronPeopleCheckpointId(
                        (checkpointIronPeopleId.values.first() as Long).toInt()
                    )
                    if (startDate != null) preferences.saveDateOfStart(startDate.time)
                }
            }
            getCachedConfig()
        }
    }

    override suspend fun getCachedConfig(): SettingsRepository.Config {
        val currentCheckpointId = preferences.getCurrentCheckpoint()
        val currentIronPeopleCheckpointId = preferences.getCurrentIronPeopleCheckpoint()
        val startDate = Date(preferences.getDateOfStart())

        settingsCache.checkpoints = checkpointDAO.getCheckpointsByType(CheckpointType.NORMAL.ordinal).map { it.toCheckpoint() }
        settingsCache.checkpointsIronPeople = checkpointDAO.getCheckpointsByType(CheckpointType.IRON.ordinal).map { it.toCheckpoint() }

        val checkpoint = settingsCache.checkpoints.find { it.id == currentCheckpointId } ?: settingsCache.checkpoints.first()
        val checkpointIronPeople = settingsCache.checkpointsIronPeople.find { it.id == currentIronPeopleCheckpointId } ?: settingsCache.checkpoints.first()

        settingsCache.currentCheckpoint = checkpoint
        settingsCache.currentIronPeopleCheckpoint = checkpointIronPeople
        settingsCache.dateOfStart = startDate

        return SettingsRepository.Config(checkpoint.id, checkpointIronPeople.id, startDate)
    }

    override suspend fun changeCurrentCheckpoint(checkpointNumber: Int) {
        settingsCache.currentCheckpoint = settingsCache.checkpoints[checkpointNumber]
        preferences.saveCurrentCheckpointId(checkpointNumber)
        if (networkUtil.isOnline()) {
            firebaseAuth.currentUser?.uid?.let { currentUserId ->
                try {
                    firestoreApi.saveCheckpointsSettings(
                        currentUserId,
                        SettingsRepository.Config(checkpointNumber, null, null)
                    )
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    override suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int) {
        settingsCache.currentIronPeopleCheckpoint =
            settingsCache.checkpointsIronPeople[checkpointNumber]
        preferences.saveCurrentIronPeopleCheckpointId(checkpointNumber)
        if (networkUtil.isOnline()) {
            firebaseAuth.currentUser?.uid?.let { currentUserId ->
                try {
                    firestoreApi.saveCheckpointsSettings(
                        currentUserId,
                        SettingsRepository.Config(null, checkpointNumber, null)
                    )
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    override suspend fun changeStartDate(date: Date) {
        settingsCache.dateOfStart = date
        preferences.saveDateOfStart(date.time)
        if (networkUtil.isOnline()) {
            try {
                firestoreApi.saveDateOfStart(date)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}