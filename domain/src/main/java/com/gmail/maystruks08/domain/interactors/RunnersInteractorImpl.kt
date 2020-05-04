package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository, private val logHelper: LogHelper) : RunnersInteractor {

    private var onRunnerChanged: ((ResultOfTask<Exception, RunnerChange>) -> Unit)? = null

    override suspend fun getRunner(id: String, type: RunnerType): ResultOfTask<Exception, Runner> {
        val runner = runnersRepository.getRunnerById(id, type) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        return ResultOfTask.build { runner }
    }

    override suspend fun getAllRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        runnersRepository.getRunners(type)

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) {
        this.onRunnerChanged = onResult
        return runnersRepository.updateRunnersCache(type, onResult)
    }

    override suspend fun addStartCheckpointToRunners(date: Date) {
        val checkpoints = runnersRepository.getStartCheckpoints()
        logHelper.log(INFO, "Add start checkpoint to all runners at: ${SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(this)}")
        mutableListOf<Runner>()
            .apply {
                addAll(runnersRepository.getNormalRunners())
                addAll(runnersRepository.getIronRunners())
            }.forEach {
                val startCheckpoint = if (it.type == RunnerType.NORMAL) checkpoints.first.first() else checkpoints.second.first()
                val checkpointsCount = if (it.type == RunnerType.NORMAL) checkpoints.first.lastIndex else checkpoints.second.lastIndex
                it.addPassedCheckpoint(CheckpointResult(startCheckpoint.id, startCheckpoint.name, startCheckpoint.type, date), checkpointsCount, true)
                onRunnerChanged?.invoke(ResultOfTask.build { RunnerChange(runnersRepository.updateRunnerData(it), Change.UPDATE) })
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String, type: RunnerType ): ResultOfTask<Exception, RunnerChange> {
        val runner = runnersRepository.getRunnerById(cardId, type) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
        val checkpointsCount = runnersRepository.getCheckpointsCount(runner.type)
        runner.addPassedCheckpoint(CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, currentCheckpoint.type, Date()), checkpointsCount)
        logHelper.log(INFO, "Add checkpoint: ${currentCheckpoint.id} to runner ${runner.id}  ${runner.type}  ${runner.fullName}")
        return ResultOfTask.build { RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE) }
    }

    override suspend fun removeCheckpointForRunner(cardId: String, checkpointId: Int, type: RunnerType): ResultOfTask<Exception, RunnerChange> {
        val runner = runnersRepository.getRunnerById(cardId, type) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        logHelper.log(INFO, "Remove checkpoint: $checkpointId for runner ${runner.id}  ${runner.type}  ${runner.fullName}")
        runner.removeCheckpoint(checkpointId)
        return ResultOfTask.build { RunnerChange(runnersRepository.updateRunnerData(runner), Change.UPDATE) }
    }

    override suspend fun getCheckpointCount(type: RunnerType): Int = runnersRepository.getCheckpointsCount(type)

    override suspend fun finishWork() {
        onRunnerChanged = null
        runnersRepository.finishWork()
    }
}