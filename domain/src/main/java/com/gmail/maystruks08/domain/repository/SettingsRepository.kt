package com.gmail.maystruks08.domain.repository

interface SettingsRepository {

    suspend fun updateConfig(): Config?

    suspend fun getConfig(): Config

    suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int)

    suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int)

    data class Config(val checkpointId: Int?, val checkpointIronPeopleId: Int?)

}