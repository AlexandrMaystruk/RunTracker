package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.DistanceStatistic

interface DistanceStatisticRepository {

    suspend fun getDistanceRunnerCount(raceId: String, distanceId: String): Int

    suspend fun getDistanceRunnerFinisherCount(raceId: String, distanceId: String): Int

    suspend fun getDistanceRunnerOffTrackCount(raceId: String, distanceId: String): Int

    suspend fun saveDistanceStatistic(raceId: String, statistic: DistanceStatistic)

}