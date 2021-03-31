package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.DistanceStatistic
import com.gmail.maystruks08.domain.repository.DistanceStatisticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculateDistanceStatisticUseCaseImpl @Inject constructor(
    private val distanceStatisticRepository: DistanceStatisticRepository
) : CalculateDistanceStatisticUseCase {

    override suspend fun invoke(raceId: String, distanceId: String) {
        withContext(Dispatchers.Default) {
            val runnerCountDeferred = async {
                distanceStatisticRepository.getDistanceRunnerCount(raceId, distanceId)
            }
            val finisherCountDeferred = async {
                distanceStatisticRepository.getDistanceRunnerFinisherCount(raceId, distanceId)
            }
            val offTrackCountDeferred = async {
                distanceStatisticRepository.getDistanceRunnerOffTrackCount(raceId, distanceId)
            }
            val runnerCount = runnerCountDeferred.await()
            val finisherCount = finisherCountDeferred.await()
            val offTrackCount = offTrackCountDeferred.await()

            distanceStatisticRepository.saveDistanceStatistic(
                raceId = raceId,
                statistic = DistanceStatistic(
                    distanceId = distanceId,
                    runnerCountInProgress = runnerCount - offTrackCount - finisherCount,
                    runnerCountOffTrack = offTrackCount,
                    finisherCount = finisherCount
                )
            )
        }
    }

}