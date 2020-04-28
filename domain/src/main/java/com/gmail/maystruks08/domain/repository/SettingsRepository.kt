package com.gmail.maystruks08.domain.repository

import java.util.*

interface SettingsRepository {

    suspend fun updateConfig(): Config?

    suspend fun getConfig(): Config

    suspend fun changeCurrentCheckpointForRunners(checkpointNumber: Int)

    suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int)

    suspend fun changeStartDate(date: Date)

    data class Config(val checkpointId: Int?, val checkpointIronPeopleId: Int?, val startDate: Date?)

}