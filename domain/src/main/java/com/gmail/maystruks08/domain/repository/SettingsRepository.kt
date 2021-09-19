package com.gmail.maystruks08.domain.repository

interface SettingsRepository {

    suspend fun getCurrentUserId(): String?

    fun getAdminUserIds(): List<String>

    fun clearCurrentSelectedRace()

    suspend fun exportRunnerDataFromXlsToRemote()

    suspend fun generateQrCodes()

}