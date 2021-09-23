package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.DistanceRepository
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeFormat
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

class ManageRunnerCheckpointInteractorImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val distanceRepository: DistanceRepository,
    private val checkpointsRepository: CheckpointsRepository,
    private val logHelper: LogHelper
) : ManageRunnerCheckpointInteractor {

    override suspend fun addCurrentCheckpointByCardId(cardId: String): Runner {
        val runner = runnersRepository.getRunnerByCardId(cardId) ?: throw RunnerNotFoundException()
        markRunnerAtCheckpoint(runner)
        return runner
    }

    override suspend fun addCurrentCheckpointByNumber(runnerNumber: String): IRunner {
        val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        markRunnerAtCheckpoint(runner)
        val teamName = runner.currentTeamName
        if(!teamName.isNullOrEmpty()){
            val team = runnersRepository.getTeam(teamName) ?: return runner
            if (team.distanceType == DistanceType.REPLAY) {
                markRunnerAtCheckpoint(team.runners.last())
                return runnersRepository.getTeam(teamName) ?: return runner
            }
            return team
        }
        return runner
    }

    override suspend fun addStartCheckpoint(currentDistance: Distance) {
        val startDate = Date()
        logHelper.log(INFO, "Add start for distance ${currentDistance.name} at ${startDate.toDateTimeFormat()}")
        distanceRepository.updateDistanceStartDate(currentDistance.id, startDate)
        runnersRepository
            .getRunnersFlow(currentDistance)
            .collect {
                val startCheckpoint = currentDistance.checkpoints.first()
                it.forEach { runner ->
                    val startCheckpointResult = CheckpointResultIml(startCheckpoint, startDate, true)
                    runner.addPassedCheckpoint(startCheckpointResult, true)
                    runnersRepository.updateRunnerData(runner)
                }
                logHelper.log(INFO, "Add start checkpoint to all runners success")
            }
    }

    override suspend fun removeCheckpoint(
        runnerNumber: String,
        checkpointId: String
    ): Change<IRunner> {
        val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.number}  actualDistanceId:${runner.actualDistanceId}  ${runner.fullName}")
        runner.removeCheckpoint(checkpointId)
        runnersRepository.updateRunnerData(runner)
        val teamName = runner.currentTeamName
        if(!teamName.isNullOrEmpty()){
            val team = runnersRepository.getTeam(teamName) ?: return Change(runner, ModifierType.UPDATE)
            return Change(team, ModifierType.UPDATE)
        }
        return Change(runner, ModifierType.UPDATE)
    }


    private suspend fun markRunnerAtCheckpoint(runner: Runner): Runner {
        val actualDistanceID = runner.actualDistanceId
        val actualRaceID = runner.actualRaceId
        val currentCheckpoint = checkpointsRepository.getCurrentCheckpoint(actualRaceID, actualDistanceID) ?: throw CheckpointNotFoundException()
        val currentDate = Date()
        runner.addPassedCheckpoint(
            checkpoint = CheckpointResultIml(checkpoint = currentCheckpoint, currentDate)
        )
        logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.getName()} to runner ${runner.number} ${runner.shortName}")
        return runnersRepository.updateRunnerData(runner)
    }

}