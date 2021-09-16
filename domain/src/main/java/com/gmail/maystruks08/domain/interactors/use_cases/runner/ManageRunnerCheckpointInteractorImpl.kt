package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeFormat
import java.util.*
import javax.inject.Inject

class ManageRunnerCheckpointInteractorImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val checkpointsRepository: CheckpointsRepository,
    private val logHelper: LogHelper
) : ManageRunnerCheckpointInteractor {

    override suspend fun addCurrentCheckpointByCardId(cardId: String): Runner {
        val runner = runnersRepository.getRunnerByCardId(cardId) ?: throw RunnerNotFoundException()
        markRunnerAtCheckpoint(runner)
        return runner
    }

    override suspend fun addCurrentCheckpointByNumber(runnerNumber: String): Runner {
        val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        markRunnerAtCheckpoint(runner)
        return runner
    }

    override suspend fun addStartCheckpoint(date: Date) {
        logHelper.log(INFO, "Add start checkpoint to all runners at: ${date.toDateTimeFormat()}")
//        val checkpoints = checkpointsRepository.getCheckpoints(distanceId)
//        runnersRepository.getRunners(raceId, distanceId).forEach {
//            it.addPassedCheckpoint(CheckpointResultIml(checkpoints.first(), date, true), true)
//            runnersRepository.updateRunnerData(it)
//        }
        logHelper.log(INFO, "Add start checkpoint to all runners success")
    }

    override suspend fun removeCheckpoint(
        runnerNumber: String,
        checkpointId: String
    ): Change<Runner> {
        val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        val actualDistanceID = runner.actualDistanceId
        logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.number}  actualDistanceId:${runner.actualDistanceId}  ${runner.fullName}")
        runner.removeCheckpoint(checkpointId)
        val teamName = runner.teamNames[actualDistanceID]
        if (!teamName.isNullOrEmpty() && !runner.offTrackDistances.any { it == actualDistanceID }) {
            runnersRepository.getRunnerTeamMembers(runner.number, teamName)?.map { teamRunner ->
                logHelper.log(INFO, "Remove checkpoint: $checkpointId for team runner ${teamRunner.number}  actualDistanceId:${runner.actualDistanceId}  ${teamRunner.fullName}")
                teamRunner.removeCheckpoint(checkpointId)
                runnersRepository.updateRunnerData(teamRunner)
            }
        }
        return Change(runnersRepository.updateRunnerData(runner), ModifierType.UPDATE)
    }


    private suspend fun markRunnerAtCheckpoint(runner: Runner): Runner {
        val actualDistanceID = runner.actualDistanceId
        val actualRaceID = runner.actualRaceId
        val currentCheckpoint = checkpointsRepository.getCurrentCheckpoint(actualRaceID, actualDistanceID) ?: throw CheckpointNotFoundException()
        val currentDate = Date()
        runner.addPassedCheckpoint(
            checkpoint = CheckpointResultIml(
                checkpoint = currentCheckpoint,
                currentDate
            )
        )
        val teamName = runner.teamNames[actualDistanceID]
        if (!teamName.isNullOrEmpty() && !runner.offTrackDistances.any { it == actualDistanceID }) {
            runnersRepository.getRunnerTeamMembers(runner.number, teamName)?.map { teamRunner ->
                teamRunner.addPassedCheckpoint(
                    checkpoint = CheckpointResultIml(
                        checkpoint = currentCheckpoint,
                        currentDate
                    )
                )
                logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.getName()} to team runner ${teamRunner.shortName}  ${teamRunner.actualDistanceId}  ${teamRunner.shortName}")
                teamRunner.currentCheckpoints?.sortBy { it.getPosition() }
                runnersRepository.updateRunnerData(teamRunner)
            }
        }
        logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.getName()} to runner ${runner.number} ${runner.shortName}")
        runner.currentCheckpoints?.sortBy { it.getPosition() }
        return runnersRepository.updateRunnerData(runner)
    }

}