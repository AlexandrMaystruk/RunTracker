package com.gmail.maystruks08.domain.repository

interface SettingsRepository {

    suspend fun getCurrentUserId(): String?

    suspend fun getAdminUserIds(): List<String>

    suspend fun getActualAppVersion(): List<String>

    suspend fun clearCurrentSelectedRace()

    suspend fun exportRunnerDataFromXlsToRemote()

    suspend fun generateQrCodes()


}