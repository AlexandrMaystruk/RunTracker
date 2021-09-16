package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.DistanceStatistic
import com.gmail.maystruks08.domain.interactors.use_cases.CalculateDistanceStatisticUseCase
import com.gmail.maystruks08.domain.repository.DistanceStatisticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculateDistanceStatisticUseCaseImpl @Inject constructor(
    private val provideCurrentRaceIdUseCase: ProvideCurrentRaceIdUseCase,
    private val distanceStatisticRepository: DistanceStatisticRepository
) : CalculateDistanceStatisticUseCase {

    override suspend fun invoke(distanceId: String?) {
        if(distanceId.isNullOrEmpty()) return
        val raceId = provideCurrentRaceIdUseCase.invoke()
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