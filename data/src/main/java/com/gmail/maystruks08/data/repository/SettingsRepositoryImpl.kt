package com.gmail.maystruks08.data.repository

import android.util.Log
import com.gmail.maystruks08.data.awaitTaskResult
import com.gmail.maystruks08.data.cache.CheckpointsCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val preferences: ConfigPreferences,
    private val checkpointsCache: CheckpointsCache,
    private val firebaseAuth: FirebaseAuth
) : SettingsRepository {

    override suspend fun updateConfig(): SettingsRepository.Config? {
        preferences.saveCurrentCheckpointId(0)
        preferences.saveCurrentIronPeopleCheckpointId(0)
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            val result = awaitTaskResult(firestoreApi.getCheckpointsSettings(currentUserId))
            val checkpointId = result["checkpointId"] as HashMap<*, *>
            val checkpointIronPeopleId = result["checkpointIronPeopleId"] as HashMap<*, *>

            preferences.saveCurrentCheckpointId((checkpointId.values.first() as Long).toInt())
            preferences.saveCurrentIronPeopleCheckpointId((checkpointIronPeopleId.values.first() as Long).toInt())

            return getConfig()
        }
        return null
    }

    override suspend fun getConfig(): SettingsRepository.Config {

        val currentCheckpointId = preferences.getCurrentCheckpoint()
        val currentIronPeopleCheckpointId = preferences.getCurrentIronPeopleCheckpoint()

        val checkpoint = checkpointsCache.checkpoints.find { it.id == currentCheckpointId } ?: checkpointsCache.checkpoints.first()
        val checkpointIronPeople = checkpointsCache.checkpointsIronPeople.find { it.id == currentIronPeopleCheckpointId } ?: checkpointsCache.checkpoints.first()

        checkpointsCache.currentCheckpoint = checkpoint
        checkpointsCache.currentIronPeopleCheckpoint = checkpointIronPeople

        return SettingsRepository.Config(checkpoint.id, checkpointIronPeople.id)
    }

    override suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int) {
        checkpointsCache.currentCheckpoint = checkpointsCache.checkpoints[checkpointNumber]
        preferences.saveCurrentCheckpointId(checkpointNumber)
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            try {
                firestoreApi.saveCheckpointsSettings(currentUserId, SettingsRepository.Config(checkpointNumber, null))
            } catch (e: Exception){
                Log.e("SettingsRepository", e.toString())
            }}
    }

    override suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int) {
        checkpointsCache.currentIronPeopleCheckpoint = checkpointsCache.checkpointsIronPeople[checkpointNumber]
        preferences.saveCurrentIronPeopleCheckpointId(checkpointNumber)
        firebaseAuth.currentUser?.uid?.let { currentUserId ->
            try {
                firestoreApi.saveCheckpointsSettings(currentUserId, SettingsRepository.Config(null, checkpointNumber))
            } catch (e: Exception) {
                Log.e("SettingsRepository", e.toString())
            }
        }
    }
}