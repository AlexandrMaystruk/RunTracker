package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.CheckpointsCache
import com.gmail.maystruks08.data.fromJsonOrNull
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.gson.Gson
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferences: ConfigPreferences,
    private val checkpointsCache: CheckpointsCache,
    private val gson: Gson
) : SettingsRepository {

    override suspend fun getInitialConfig(): SettingsRepository.Config {
        val currentCheckpoint = gson.fromJsonOrNull<Checkpoint>(preferences.getCurrentCheckpoint())
        val currentIronPeopleCheckpoint = gson.fromJsonOrNull<Checkpoint>(preferences.getCurrentIronPeopleCheckpoint())
        val checkpointNumber = checkpointsCache.checkpointsList.indexOfFirst { it.id ==  currentCheckpoint?.id}
        val checkpointIronPeopleNumber = checkpointsCache.checkpointsIronPeopleList.indexOfFirst { it.id ==  currentIronPeopleCheckpoint?.id}
        return if(checkpointNumber != -1 && checkpointIronPeopleNumber != -1){
            checkpointsCache.currentCheckpoint = currentCheckpoint!!
            checkpointsCache.currentIronPeopleCheckpoint = currentIronPeopleCheckpoint!!
            SettingsRepository.Config(checkpointNumber, checkpointIronPeopleNumber)
        } else SettingsRepository.Config(0, 0)
    }

    override suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int) {
        checkpointsCache.currentCheckpoint = checkpointsCache.checkpointsList[checkpointNumber]
        preferences.saveCurrentCheckpoint(gson.toJson(checkpointsCache.checkpointsList[checkpointNumber]))
    }

    override suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int) {
        checkpointsCache.currentIronPeopleCheckpoint = checkpointsCache.checkpointsIronPeopleList[checkpointNumber]
        preferences.saveCurrentIronPeopleCheckpoint(gson.toJson(checkpointsCache.checkpointsIronPeopleList[checkpointNumber]))
    }
}