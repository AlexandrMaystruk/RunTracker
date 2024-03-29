package com.gmail.maystruks08.data.local

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigPreferences @Inject constructor(context: Context) {

    companion object {

        private const val NAME = "CONFIG_PREFS"
        //separated fetching logic
        private const val CHECKPOINT = "CHECKPOINT"
        private const val IRON_PEOPLE_CHECKPOINT = "IRON_PEOPLE_CHECKPOINT"

        private const val DATE_OF_START = "DATE_OF_START"

        private const val CURRENT_RACE_ID = "CURRENT_RACE_ID"
        private const val CURRENT_RACE_NAME = "CURRENT_RACE_NAME"


        private const val ADMIN_USER_IDS = "ADMIN_USER_IDS"

    }

    private val sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveAdminUserIds(adminUserIds: String) {
        sp.edit().apply {
            putString(ADMIN_USER_IDS, adminUserIds)
            apply()
        }
    }

    fun saveCurrentCheckpointId(checkpoint: Int?) {
        sp.edit().apply {
            putInt(CHECKPOINT, checkpoint?:0)
            apply()
        }
    }

    fun getCurrentCheckpoint(): Int{
        return sp.getInt(CHECKPOINT, 0)
    }

    fun saveCurrentIronPeopleCheckpointId(checkpoint: Int?) {
        sp.edit().apply {
            putInt(IRON_PEOPLE_CHECKPOINT, checkpoint?:0)
            apply()
        }
    }

    fun getCurrentIronPeopleCheckpoint(): Int {
        return sp.getInt(IRON_PEOPLE_CHECKPOINT, 0)
    }

    fun saveDateOfStart(date: Long) {
        sp.edit().apply {
            putLong(DATE_OF_START, date)
            apply()
        }
    }

    fun getDateOfStart(): Long {
        return sp.getLong(DATE_OF_START, 0)
    }

    fun getAdminUserIds(): String? {
        return sp.getString(ADMIN_USER_IDS, "")
    }

    fun saveRaceId(raceId: String) {
        sp.edit().apply {
            putString(CURRENT_RACE_ID, raceId)
            apply()
        }
    }

    fun saveRaceName(raceName: String) {
        sp.edit().apply {
            putString(CURRENT_RACE_NAME, raceName)
            apply()
        }
    }

    fun getRaceId(): String {
        return sp.getString(CURRENT_RACE_ID, null) ?: "-1"
    }

    fun getRaceName(): String {
        return sp.getString(CURRENT_RACE_NAME, null) ?: "-1"
    }

    fun clearRaceId() {
        sp.edit().apply {
            remove(CURRENT_RACE_ID)
            apply()
        }
    }

    fun clearRaceName() {
        sp.edit().apply {
            remove(CURRENT_RACE_NAME)
            apply()
        }
    }
}