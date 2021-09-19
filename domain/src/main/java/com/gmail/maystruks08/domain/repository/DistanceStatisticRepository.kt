package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Statistic

interface DistanceStatisticRepository {

    suspend fun getDistanceRunnerCount(raceId: String, distanceId: String): Int
    suspend fun getDistanceRunnerFinisherCount(raceId: String, distanceId: String): Int
    suspend fun getDistanceRunnerOffTrackCount(raceId: String, distanceId: String): Int

    suspend fun getCheckpointRunnerFinisherCount(raceId: String, distanceId: String, checkpointId: String): Int

    suspend fun saveDistanceStatistic(raceId: String, statistic: Statistic)

}