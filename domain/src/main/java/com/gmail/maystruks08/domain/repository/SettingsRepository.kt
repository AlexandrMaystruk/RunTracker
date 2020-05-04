package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.ResultOfTask
import java.util.*

interface SettingsRepository {

    suspend fun updateConfig(): ResultOfTask<Exception, Config>

    suspend fun getConfig(): Config

    suspend fun changeCurrentCheckpoint(checkpointNumber: Int)

    suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int)

    suspend fun changeStartDate(date: Date)

    data class Config(val checkpointId: Int?, val checkpointIronPeopleId: Int?, val startDate: Date?)

}