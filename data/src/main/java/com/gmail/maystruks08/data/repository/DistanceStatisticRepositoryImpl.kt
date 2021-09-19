package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.domain.entities.Statistic
import com.gmail.maystruks08.domain.repository.DistanceStatisticRepository
import javax.inject.Inject

class DistanceStatisticRepositoryImpl @Inject constructor(
    private val distanceDAO: DistanceDAO
) : DistanceStatisticRepository {

    override suspend fun getDistanceRunnerCount(raceId: String, distanceId: String): Int {
        return distanceDAO.getRunnersCount(raceId, distanceId)
    }

    override suspend fun getDistanceRunnerFinisherCount(raceId: String, distanceId: String): Int {
        return distanceDAO.getFinisherCount(raceId, distanceId)
    }

    override suspend fun getDistanceRunnerOffTrackCount(raceId: String, distanceId: String): Int {
        return distanceDAO.getRunnerCountOffTrack(raceId, distanceId)
    }

    override suspend fun getCheckpointRunnerFinisherCount(
        raceId: String,
        distanceId: String,
        checkpointId: String
    ): Int {
        return distanceDAO.getFinisherCountAtCheckpoint(distanceId, checkpointId)
    }

    override suspend fun saveDistanceStatistic(raceId: String, statistic: Statistic) {
        val statisticTable = distanceDAO.getDistanceStatistic(raceId, statistic.distanceId)
        if (
            statisticTable?.finisherCount != statistic.finisherCount ||
            statisticTable.runnerCountInProgress != statistic.runnerCountInProgress ||
            statisticTable.runnerCountOffTrack != statistic.runnerCountOffTrack
        ) {
            distanceDAO.updateDistanceStatistic(
                raceId = raceId,
                distanceId = statistic.distanceId,
                runnerCountInProgress = statistic.runnerCountInProgress,
                runnerCountOffTrack = statistic.runnerCountOffTrack,
                finisherCount = statistic.finisherCount
            )
        }
    }
}