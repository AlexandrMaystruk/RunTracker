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

    private var onRunnerChanged: ((ResultOfTask<Exception, RunnerChange>) -> Unit)? = null

    override suspend fun getRunner(id: String): ResultOfTask<Exception, Runner> =
         ResultOfTask.build { runnersRepository.getRunnerById(id) ?: throw  RunnerNotFoundException()}

    override suspend fun getRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        ResultOfTask.build { runnersRepository.getRunners(type) }

    override suspend fun getFinishers(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        ResultOfTask.build { runnersRepository.getRunners(type, true) }

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) {
        this.onRunnerChanged = onResult
        return runnersRepository.updateRunnersCache(type, onResult)
    }

    override suspend fun addStartCheckpointToRunners(date: Date) {
        val checkpoints = runnersRepository.getCheckpoints(RunnerType.NORMAL)
        val ironCheckpoints = runnersRepository.getCheckpoints(RunnerType.IRON)
        logHelper.log(INFO, "Add start checkpoint to all runners at: ${date.toDateTimeShortFormat()}")
        mutableListOf<Runner>()
            .apply {
                addAll(runnersRepository.getRunners(RunnerType.NORMAL))
                addAll(runnersRepository.getRunners(RunnerType.IRON))
            }.forEach {
                val startCheckpoint = if (it.type == RunnerType.NORMAL) checkpoints.first() else ironCheckpoints.first()
                val checkpointsCount = if (it.type == RunnerType.NORMAL) checkpoints.lastIndex else ironCheckpoints.lastIndex
                it.addPassedCheckpoint(checkpoint = CheckpointResult(startCheckpoint.id, startCheckpoint.name, startCheckpoint.type, date), checkpointsCount =  checkpointsCount, isRestart = true)
                onRunnerChanged?.invoke(ResultOfTask.build { RunnerChange(runnersRepository.updateRunnerData(it), Change.UPDATE) })
        }
    }

    override suspend fun markRunnerGotOffTheRoute(cardId: String): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerById(cardId) ?: throw RunnerNotFoundException()
            runner.markThatRunnerIsOffTrack()
            logHelper.log(INFO, "Runner ${runner.id} ${runner.type} ${runner.fullName} is off track")
            if(!runner.teamName.isNullOrEmpty()){
                runnersRepository.getRunnerTeamMembers(runner.id, runner.teamName)?.map { teamRunner ->
                    teamRunner.markThatRunnerIsOffTrack()
                    logHelper.log(INFO, "Team runner ${runner.id} ${runner.type} ${runner.fullName} is off track")
                    runnersRepository.updateRunnerData(teamRunner)
                }
            }
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String ): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerById(cardId) ?: throw RunnerNotFoundException()
            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
            val checkpointsCount = runnersRepository.getCheckpoints(runner.type).size
            val currentDate = Date()
            runner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
            if(!runner.teamName.isNullOrEmpty()){
               runnersRepository.getRunnerTeamMembers(runner.id, runner.teamName)?.map { teamRunner ->
                   teamRunner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
                   logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to team runner ${teamRunner.id}  ${teamRunner.type}  ${teamRunner.fullName}")
                   runnersRepository.updateRunnerData(teamRunner)
                }
            }
            logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.id}  ${runner.type}  ${runner.fullName}")
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun removeCheckpointForRunner(cardId: String, checkpointId: Int): ResultOfTask<Exception, RunnerChange> {
        return ResultOfTask.build {
            val runner = runnersRepository.getRunnerById(cardId) ?: throw RunnerNotFoundException()
            logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.id}  ${runner.type}  ${runner.fullName}")
            runner.removeCheckpoint(checkpointId)
            if(!runner.teamName.isNullOrEmpty()){
                runnersRepository.getRunnerTeamMembers(runner.id, runner.teamName)?.map { teamRunner ->
                    logHelper.log(INFO, "Remove checkpoint: $checkpointId for team runner ${teamRunner.id}  ${teamRunner.type}  ${teamRunner.fullName}")
                    teamRunner.removeCheckpoint(checkpointId)
                    runnersRepository.updateRunnerData(teamRunner)
                }
            }
            RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE)
        }
    }

    override suspend fun finishWork() {
        onRunnerChanged = null
        runnersRepository.finishWork()
    }
}