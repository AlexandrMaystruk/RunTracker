package com.gmail.maystruks08.domain.repository

interface SettingsRepository {

    suspend fun getInitialConfig(): Config

    suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int)

    suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int)

    data class Config(val checkpointNumber: Int, val checkpointIronPeopleNumber: Int)

}