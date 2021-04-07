package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.ApplicationCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferences: ConfigPreferences,
    private val applicationCache: ApplicationCache,
    private val firebaseAuth: FirebaseAuth,
    private val checkpointDAO: CheckpointDAO,
    private val networkUtil: NetworkUtil
) : SettingsRepository {


    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getAdminUserIds(): List<String> {
        return emptyList()
    }
}