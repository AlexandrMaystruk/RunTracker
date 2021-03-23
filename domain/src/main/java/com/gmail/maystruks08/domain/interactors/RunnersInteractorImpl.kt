package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeFormat
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(
    private val runnersRepository: RunnersRepository,
    private val checkpointsRepository: CheckpointsRepository,
    private val logHelper: LogHelper
) : RunnersInteractor {

    override suspend fun observeRunnerDataFlow(currentRaceId: String) {
        runnersRepository.observeRunnerData(currentRaceId)
    }

    override suspend fun getRunner(runnerNumber: Long): TaskResult<Exception, Runner> =
        TaskResult.build {
            runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        }

    override suspend fun getRunnersFlow(distanceId: String): Flow<List<Runner>> {
        return runnersRepository.getRunnersFlow(distanceId = distanceId)
    }

    override suspend fun getRunners(
        distanceId: String,
        query: String
    ): TaskResult<Exception, List<Runner>> {
        return TaskResult.build {
            runnersRepository.getRunners(distanceId = distanceId, query = query)
        }
    }

    override suspend fun getFinishersFlow(distanceId: String): Flow<List<Runner>> {
        return runnersRepository.getRunnersFlow(
            distanceId = distanceId,
            onlyFinishers = true
        )/*.sortedBy { it.totalResult } }*/
    }

    override suspend fun getFinishers(
        distanceId: String,
        query: String
    ): TaskResult<Exception, List<Runner>> {
        return TaskResult.build {
            runnersRepository.getRunners(
                distanceId = distanceId,
                query = query,
                onlyFinishers = true
            )
        }
    }

    override suspend fun addStartCheckpointToRunners(
        raceId: String,
        distanceId: String,
        date: Date
    ): TaskResult<Exception, Unit> {
        return TaskResult.build {
            logHelper.log(INFO, "Add start checkpoint to all runners at: ${date.toDateTimeFormat()}")
            val checkpoints = checkpointsRepository.getCheckpoints(raceId, distanceId)
            runnersRepository.getRunners(raceId, distanceId).forEach {
                it.addPassedCheckpoint(CheckpointResultIml(checkpoints.first(), date, true), true)
                runnersRepository.updateRunnerData(it)
            }
            logHelper.log(INFO, "Add start checkpoint to all runners success")
        }
    }

    override suspend fun changeRunnerCardId(
        runnerNumber: Long,
        newCardId: String
    ): TaskResult<Exception, Change<Runner>> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            val oldRunnerCardId = runner.cardId
            runner.updateCardId(newCardId)
            logHelper.log(INFO, "Attach new card with id $newCardId to runner ${runner.fullName}. Old card id = $oldRunnerCardId")
            Change(runnersRepository.updateRunnerData(runner), ModifierType.UPDATE)
        }
    }

    override suspend fun markRunnerGotOffTheRoute(runnerNumber: Long): TaskResult<Exception, Runner> {
        return TaskResult.build {
            val runner =
                runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            runner.markThatRunnerIsOffTrack()
            logHelper.log(INFO, "Runner ${runner.number} ${runner.fullName} is off track")
            runnersRepository.updateRunnerData(runner)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String): TaskResult<Exception, Runner> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByCardId(cardId) ?: throw RunnerNotFoundException()
            markRunnerAtCheckpoint(runner)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(runnerNumber: Long): TaskResult<Exception, Runner> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            markRunnerAtCheckpoint(runner)
        }
    }

    override suspend fun removeCheckpointForRunner(
        runnerNumber: Long,
        checkpointId: String
    ): TaskResult<Exception, Change<Runner>> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            val actualDistanceID = runner.actualDistanceId
            logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.number}  actualDistanceId:${runner.actualDistanceId}  ${runner.fullName}")
            runner.removeCheckpoint(checkpointId)
            val teamName = runner.teamNames[actualDistanceID]
            if(!teamName.isNullOrEmpty() &&  runner.isOffTrack[actualDistanceID] != true){
                runnersRepository.getRunnerTeamMembers(runner.number, teamName)?.map { teamRunner ->
                    logHelper.log(INFO, "Remove checkpoint: $checkpointId for team runner ${teamRunner.number}  actualDistanceId:${runner.actualDistanceId}  ${teamRunner.fullName}")
                    teamRunner.removeCheckpoint(checkpointId)
                    runnersRepository.updateRunnerData(teamRunner)
                }
            }
            Change(runnersRepository.updateRunnerData(runner), ModifierType.UPDATE)
        }
    }


    private suspend fun markRunnerAtCheckpoint(runner: Runner): Runner {
        val actualDistanceID = runner.actualDistanceId
        val actualRaceID = runner.actualRaceId
        val currentCheckpoint = checkpointsRepository.getCurrentCheckpoint(actualRaceID, actualDistanceID)?: throw CheckpointNotFoundException()
        val currentDate = Date()
        runner.addPassedCheckpoint(checkpoint = CheckpointResultIml(checkpoint = currentCheckpoint, currentDate))
        val teamName = runner.teamNames[actualDistanceID]
        if(!teamName.isNullOrEmpty() && runner.isOffTrack[actualDistanceID] != true){
            runnersRepository.getRunnerTeamMembers(runner.number, teamName)?.map { teamRunner ->
                teamRunner.addPassedCheckpoint(checkpoint = CheckpointResultIml(checkpoint = currentCheckpoint, currentDate))
                logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.getName()} to team runner ${teamRunner.shortName}  ${teamRunner.actualDistanceId}  ${teamRunner.shortName}")
                runnersRepository.updateRunnerData(teamRunner)
            }
        }
        logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.getName()} to runner ${runner.number} ${runner.shortName}")
        return runnersRepository.updateRunnerData(runner)
    }
}