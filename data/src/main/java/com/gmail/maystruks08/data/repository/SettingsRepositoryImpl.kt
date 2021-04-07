package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferences: ConfigPreferences,
    private val firebaseAuth: FirebaseAuth,
) : SettingsRepository {


    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getAdminUserIds(): List<String> {
        return emptyList()
    }

    override fun clearCurrentSelectedRace() {
        preferences.clearRaceId()
        preferences.clearRaceName()
    }
}