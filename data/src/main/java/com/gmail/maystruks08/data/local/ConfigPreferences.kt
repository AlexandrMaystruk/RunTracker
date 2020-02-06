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
}