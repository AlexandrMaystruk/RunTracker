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

    }

    private val sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveCurrentCheckpoint(checkpoint: String?) {
        sp.edit().apply {
            putString(CHECKPOINT, checkpoint)
            apply()
        }
    }

    fun getCurrentCheckpoint(): String? {
        return sp.getString(CHECKPOINT, null)
    }

    fun saveCurrentIronPeopleCheckpoint(checkpoint: String?) {
        sp.edit().apply {
            putString(IRON_PEOPLE_CHECKPOINT, checkpoint)
            apply()
        }
    }

    fun getCurrentIronPeopleCheckpoint(): String? {
        return sp.getString(IRON_PEOPLE_CHECKPOINT, null)
    }
}