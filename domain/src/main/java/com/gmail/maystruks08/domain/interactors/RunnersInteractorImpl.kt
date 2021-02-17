package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import java.util.*
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository, private val logHelper: LogHelper) : RunnersInteractor {

    override suspend fun getRunner(runnerNumber: Long): TaskResult<Exception, Runner> =
         TaskResult.build { runnersRepository.getRunnerByNumber(runnerNumber) ?: throw  RunnerNotFoundException()}

    override suspend fun getRunners(distanceId: Long, initSize: Int?): TaskResult<Exception, List<Runner>> =
        TaskResult.build {
            runnersRepository.getRunners(distanceId = distanceId, initSize = initSize)
        }

    override suspend fun getFinishers(distanceId: Long): TaskResult<Exception, List<Runner>> =
        TaskResult.build { runnersRepository.getRunners(distanceId, true).sortedBy { it.totalResult } }

    override suspend fun addStartCheckpointToRunners(date: Date): TaskResult<Exception, Unit>{
       return TaskResult.build {
//            val checkpoints = runnersRepository.getCheckpoints(RunnerType.NORMAL)
//            logHelper.log(INFO, "Add start checkpoint to all runners at: ${date.toDateTimeShortFormat()}")
//            mutableListOf<Runner>()
//                .apply {
//                    addAll(runnersRepository.getRunners(RunnerType.NORMAL))
//                }.forEach {
//                    val startCheckpoint = checkpoints.first()
//                    val checkpointsCount = checkpoints.lastIndex
//                    it.addPassedCheckpoint(checkpoint = CheckpointResult(startCheckpoint.id, startCheckpoint.name, startCheckpoint.type, date), checkpointsCount = checkpointsCount, isRestart = true)
//                    runnersRepository.updateRunnerData(it)
//                }
        }
    }

    override suspend fun changeRunnerCardId(runnerNumber: Long, newCardId: String): TaskResult<Exception,  Change<Runner>> {
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
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
            runner.markThatRunnerIsOffTrack()
            logHelper.log(INFO, "Runner ${runner.number} ${runner.fullName} is off track")
            runnersRepository.updateRunnerData(runner)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String ): TaskResult<Exception, Runner> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByCardId(cardId) ?: throw RunnerNotFoundException()
//            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
//            val checkpointsCount = runnersRepository.getCheckpoints(runner.type).size
//            val currentDate = Date()
//            runner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
//            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
//               runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
//                   teamRunner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
//                   logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
//                   runnersRepository.updateRunnerData(teamRunner)
//                }
//            }
//            logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.number}  ${runner.type}  ${runner.fullName}")
            runnersRepository.updateRunnerData(runner)
        }
    }

    override suspend fun addCurrentCheckpointToRunner(runnerNumber: Long): TaskResult<Exception, Runner>{
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
//            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
//            val checkpointsCount = runnersRepository.getCheckpoints(runner.type).size
//            val currentDate = Date()
//            runner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
//            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
//                runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
//                    teamRunner.addPassedCheckpoint(checkpoint = CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, currentDate), checkpointsCount = checkpointsCount)
//                    logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
//                    runnersRepository.updateRunnerData(teamRunner)
//                }
//            }
//            logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.number} ${runner.fullName}")
            runnersRepository.updateRunnerData(runner)
        }
    }

    override suspend fun removeCheckpointForRunner(runnerNumber: Long, checkpointId: Long): TaskResult<Exception, Change<Runner>> {
        return TaskResult.build {
            val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
//            logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.number}  ${runner.type}  ${runner.fullName}")
//            runner.removeCheckpoint(checkpointId)
//            if(!runner.teamName.isNullOrEmpty() && !runner.isOffTrack){
//                runnersRepository.getRunnerTeamMembers(runner.number, runner.teamName)?.map { teamRunner ->
//                    logHelper.log(INFO, "Remove checkpoint: $checkpointId for team runner ${teamRunner.number}  ${teamRunner.type}  ${teamRunner.fullName}")
//                    teamRunner.removeCheckpoint(checkpointId)
//                    runnersRepository.updateRunnerData(teamRunner)
//                }
//            }
            Change(runnersRepository.updateRunnerData(runner), ModifierType.UPDATE)
        }
    }
}