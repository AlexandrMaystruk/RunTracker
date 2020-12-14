package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.awaitTaskResult
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.remote.pojo.CheckpointPojo
import com.gmail.maystruks08.data.toDataClass
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class SettingsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val preferences: ConfigPreferences,
    private val settingsCache: SettingsCache,
    private val firebaseAuth: FirebaseAuth,
    private val checkpointDAO: CheckpointDAO,
    private val networkUtil: NetworkUtil
) : SettingsRepository {

    override suspend fun updateConfig(): TaskResult<Exception, Unit> {
        return TaskResult.build {
            if (networkUtil.isOnline()) {
                val checkpointsSnapshot = awaitTaskResult(firestoreApi.getCheckpoints())
                checkpointsSnapshot.data?.toDataClass<HashMap<String, CheckpointPojo>?>()
                    ?.let { hashMap ->
                        val checkpoints = hashMap.values
                        checkpointDAO.deleteCheckpoints()
                        checkpointDAO.insertAllOrReplace(checkpoints.map { it.toCheckpointTable() })
                    }
                firebaseAuth.currentUser?.uid?.let { currentUserId ->
                    val checkpointsSettingsDocument = awaitTaskResult(firestoreApi.getCheckpointsSettings(currentUserId))
                    val startDateResultDocument = awaitTaskResult(firestoreApi.getDateOfStart())
                    val adminUserIdsDocument = awaitTaskResult(firestoreApi.getAdminUserIds())

                    val checkpointId = checkpointsSettingsDocument["checkpointId"] as? HashMap<*, *>
                    val checkpointIronPeopleId = checkpointsSettingsDocument["checkpointIronPeopleId"] as? HashMap<*, *>
                    val adminUserIds = adminUserIdsDocument.get("UUID") as? ArrayList<*>
                    val startDate = (startDateResultDocument["Start at"] as? com.google.firebase.Timestamp)?.toDate()

                    if (checkpointId != null) preferences.saveCurrentCheckpointId((checkpointId.values.first() as Long).toInt())
                    if (checkpointIronPeopleId != null) preferences.saveCurrentIronPeopleCheckpointId((checkpointIronPeopleId.values.first() as Long).toInt())
                    if (startDate != null) preferences.saveDateOfStart(startDate.time)
                    if (!adminUserIds.isNullOrEmpty()) preferences.saveAdminUserIds(Gson().toJson(adminUserIds, object : TypeToken<List<String>?>() {}.type))

                    Timber.e(adminUserIds?.toTypedArray()?.contentToString())
                }
            }
        }
    }

    override suspend fun getCachedConfig(): TaskResult<Exception, SettingsRepository.CheckpointsConfig> {
        return TaskResult.build {
            val currentCheckpointId = preferences.getCurrentCheckpoint()
            val currentIronPeopleCheckpointId = preferences.getCurrentIronPeopleCheckpoint()
            val startDate = Date(preferences.getDateOfStart())
            val adminUserIds: List<String>? = Gson().fromJson(preferences.getAdminUserIds(), object : TypeToken<List<String>?>() {}.type)

            settingsCache.adminUserIds.clear()
            adminUserIds?.let { settingsCache.adminUserIds.addAll(it) }

//            settingsCache.checkpoints = checkpointDAO.getCheckpointsByType(CheckpointType.NORMAL.ordinal).map { it.toCheckpoint() }.sortedBy { it.id }
//            settingsCache.checkpointsIronPeople = checkpointDAO.getCheckpointsByType(CheckpointType.IRON.ordinal).map { it.toCheckpoint() }.sortedBy { it.id }
//
//            val checkpoint = settingsCache.checkpoints.find { it.id == currentCheckpointId } ?: settingsCache.checkpoints.first()
//            val checkpointIronPeople = settingsCache.checkpointsIronPeople.find { it.id == currentIronPeopleCheckpointId } ?: settingsCache.checkpointsIronPeople.first()
//
//            settingsCache.currentCheckpoint = checkpoint
//            settingsCache.currentIronPeopleCheckpoint = checkpointIronPeople
//            settingsCache.dateOfStart = startDate
//
            val config = SettingsRepository.Config(0, 1, startDate)
//            val checkpointsTitle = settingsCache.checkpoints.map { it.name }
//            val ironCheckpointsTitle = settingsCache.checkpointsIronPeople.map { it.name }

            SettingsRepository.CheckpointsConfig(listOf(), listOf(), config)
        }
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
        settingsCache.currentIronPeopleCheckpoint = settingsCache.checkpointsIronPeople[checkpointNumber]
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

    override fun getAdminUserIds(): List<String> {
        return settingsCache.adminUserIds
    }
}