package com.gmail.maystruks08.data.repository

import android.util.Log
import com.gmail.maystruks08.data.awaitTaskResult
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class SettingsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val preferences: ConfigPreferences,
    private val settingsCache: SettingsCache,
    private val firebaseAuth: FirebaseAuth
) : SettingsRepository {

    override suspend fun updateConfig(): SettingsRepository.Config? {
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            val result = awaitTaskResult(firestoreApi.getCheckpointsSettings(currentUserId))
            val startDateResult = awaitTaskResult(firestoreApi.getDateOfStart())

            val checkpointId = result["checkpointId"] as HashMap<*, *>
            val checkpointIronPeopleId = result["checkpointIronPeopleId"] as HashMap<*, *>
            val startDate = startDateResult["Start at"] as HashMap<*, *>

            preferences.saveCurrentCheckpointId((checkpointId.values.first() as Long).toInt())
            preferences.saveCurrentIronPeopleCheckpointId((checkpointIronPeopleId.values.first() as Long).toInt())
            preferences.saveDateOfStart((startDate.values.first() as Date).time)

            return getConfig()
        }
        return null
    }

    override suspend fun getConfig(): SettingsRepository.Config {

        val currentCheckpointId = preferences.getCurrentCheckpoint()
        val currentIronPeopleCheckpointId = preferences.getCurrentIronPeopleCheckpoint()
        val startDate = Date(preferences.getDateOfStart())

        val checkpoint = settingsCache.checkpoints.find { it.id == currentCheckpointId } ?: settingsCache.checkpoints.first()
        val checkpointIronPeople = settingsCache.checkpointsIronPeople.find { it.id == currentIronPeopleCheckpointId } ?: settingsCache.checkpoints.first()

        settingsCache.currentCheckpoint = checkpoint
        settingsCache.currentIronPeopleCheckpoint = checkpointIronPeople
        settingsCache.dateOfStart = startDate

        return SettingsRepository.Config(checkpoint.id, checkpointIronPeople.id, startDate)
    }

    override suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int) {
        settingsCache.currentCheckpoint = settingsCache.checkpoints[checkpointNumber]
        preferences.saveCurrentCheckpointId(checkpointNumber)
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            try {
                firestoreApi.saveCheckpointsSettings(currentUserId, SettingsRepository.Config(checkpointNumber, null, null))
            } catch (e: Exception){
                Log.e("SettingsRepository", e.toString())
            }}
    }

    override suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int) {
        settingsCache.currentIronPeopleCheckpoint = settingsCache.checkpointsIronPeople[checkpointNumber]
        preferences.saveCurrentIronPeopleCheckpointId(checkpointNumber)
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            try {
                firestoreApi.saveCheckpointsSettings(currentUserId, SettingsRepository.Config(null, checkpointNumber, null))
            } catch (e: Exception) {
                Log.e("SettingsRepository", e.toString())
            }
        }
    }

    override suspend fun changeStartDate(date: Date) {
        settingsCache.dateOfStart = date
        preferences.saveDateOfStart(date.time)
        try {
            firestoreApi.saveDateOfStart(date)
        } catch (e: Exception) {
            Log.e("SettingsRepository", e.toString())
        }
    }
}