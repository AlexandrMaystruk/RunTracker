package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeShortFormat
import java.util.*
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository, private val logHelper: LogHelper) : RunnersInteractor {

    override suspend fun getRunner(runnerNumber: Int): ResultOfTask<Exception, Runner> =
         ResultOfTask.build { runnersRepository.getRunnerByNumber(runnerNumber) ?: throw  RunnerNotFoundException()}

    override suspend fun getRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        ResultOfTask.build { runnersRepository.getRunners(type).sortedBy { runner -> runner.checkpoints.count { it is CheckpointResult } }.sortedBy { it.totalResult }.sortedBy { it.isOffTrack }}

    override suspend fun getFinishers(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        ResultOfTask.build { runnersRepository.getRunners(type, true).sortedBy { it.totalResult } }

    override suspend fun addStartCheckpointToRunners(date: Date): ResultOfTask<Exception, Unit>{
       return ResultOfTask.build {
            val checkpoints = runnersRepository.getCheckpoints(RunnerType.NORMAL)
            logHelper.log(INFO, "Add start checkpoint to all runners at: ${date.toDateTimeShortFormat()}")
            mutableListOf<Runner>()
                .apply {
                    addAll(runnersRepository.getRunners(RunnerType.NORMAL))
                }.forEach {
                    val startCheckpoint = checkpoints.first()
                    val checkpointsCount = checkpoints.lastIndex
                    it.addPassedCheckpoint(checkpoint = CheckpointResult(startCheckpoint.id, startCheckpoint.name, startCheckpoint.type, date), checkpointsCount = checkpointsCount, isRestart = true)
                    runnersRepository.updateRunnerData(it)
                }
        }
    }

    override suspend fun changeRunnerCardId(runnerNumber: Int, newCardId: String): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            val oldRunnerCardId = runner.cardId
            runner.updateCardId(newCardId)
            logHelper.log(INFO, "Attach new card with id $newCardId to runner ${runner.type} ${runner.fullName}. Old card id = $oldRunnerCardId")
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun markRunnerGotOffTheRoute(runnerNumber: Int): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            runner.markThatRunnerIsOffTrack()
            logHelper.log(INFO, "Runner ${runner.number} ${runner.type} ${runner.fullName} is off track")
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String ): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerByCardId(cardId) ?: throw RunnerNotFoundException()
            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
            val checkpointsCount = runnersRepository.getCheckpoints(runner.type).size
            val currentDate = Date()
            runner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
               runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
                   teamRunner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
                   logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
                   runnersRepository.updateRunnerData(teamRunner)
                }
            }
            logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.number}  ${runner.type}  ${runner.fullName}")
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(runnerNumber: Int): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
            val checkpointsCount = runnersRepository.getCheckpoints(runner.type).size
            val currentDate = Date()
            runner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
                runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
                    teamRunner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
                    logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
                    runnersRepository.updateRunnerData(teamRunner)
                }
            }
            logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.number}  ${runner.type}  ${runner.fullName}")
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun removeCheckpointForRunner(runnerNumber: Int, checkpointId: Int): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.number}  ${runner.type}  ${runner.fullName}")
            runner.removeCheckpoint(checkpointId)
            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
                runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
                    logHelper.log(INFO, "Remove checkpoint: $checkpointId for team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
                    teamRunner.removeCheckpoint(checkpointId)
                    runnersRepository.updateRunnerData(teamRunner)
                }
            }
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }
}