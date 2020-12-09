package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.DistanceSettings
import com.gmail.maystruks08.domain.entities.TaskResult
import java.util.*

interface SettingsRepository {

    suspend fun updateConfig(): TaskResult<Exception, Unit>

    suspend fun getCachedConfig(): TaskResult<Exception, CheckpointsConfig>

    suspend fun changeCurrentCheckpoint(checkpointNumber: Int)

    suspend fun changeCurrentCheckpointForIronPeoples(checkpointNumber: Int)

    suspend fun changeStartDate(date: Date)

    fun getAdminUserIds(): List<String>

    suspend fun getRaceList(): TaskResult<Exception, List<DistanceSettings>>


    suspend fun getRaceCheckpoints(raceId: String): TaskResult<Exception, DistanceSettings>


    data class CheckpointsConfig(
        val checkpointsName: List<String>,
        val ironCheckpointsName: List<String>,
        val settings: Config
    )

    data class Config(
        val checkpointId: Int?,
        val checkpointIronPeopleId: Int?,
        val startDate: Date?
    )

}