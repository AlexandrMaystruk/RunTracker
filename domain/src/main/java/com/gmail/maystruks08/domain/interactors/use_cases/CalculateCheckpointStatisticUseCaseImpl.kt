package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.CheckpointStatistic
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.repository.DistanceStatisticRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculateCheckpointStatisticUseCaseImpl @Inject constructor(
    private val provideCurrentRaceIdUseCase: ProvideCurrentRaceIdUseCase,
    private val distanceStatisticRepository: DistanceStatisticRepository
) : CalculateCheckpointStatisticUseCase {

    override suspend fun invoke(checkpoints: MutableList<Checkpoint>): List<CheckpointStatistic> {
        val raceId = provideCurrentRaceIdUseCase.invoke()
        return withContext(Dispatchers.IO) {
            val distanceId = checkpoints.first().getDistanceId()
            val runnerCountDeferred = async { distanceStatisticRepository.getDistanceRunnerCount(raceId, distanceId) }
            val offTrackCountDeferred = async { distanceStatisticRepository.getDistanceRunnerOffTrackCount(raceId, distanceId) }
            val runnerCount = runnerCountDeferred.await()
            val offTrackCount = offTrackCountDeferred.await()
            return@withContext checkpoints.map {
                val finisherCountDeferred = async {
                    distanceStatisticRepository.getCheckpointRunnerFinisherCount(
                        raceId,
                        it.getDistanceId(),
                        it.getId()
                    )
                }
                val finisherCount = finisherCountDeferred.await()
                val runnerCountInProgress = runnerCount - finisherCount - offTrackCount
                CheckpointStatistic(it.getId(), it.getName(), runnerCountInProgress, finisherCount)
            }
        }
    }
}