package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import java.util.*
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository) : RunnersInteractor {

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
        mutableListOf<Runner>()
            .apply {
                addAll(runnersRepository.getNormalRunners())
                addAll(runnersRepository.getIronRunners())
            }.forEach {
                val startCheckpoint = if (it.type == RunnerType.NORMAL) checkpoints.first.first() else checkpoints.second.first()
                val checkpointsCount = if (it.type == RunnerType.NORMAL) checkpoints.first.lastIndex else checkpoints.second.lastIndex
                it.addPassedCheckpoint(CheckpointResult(startCheckpoint.id, startCheckpoint.name, date), checkpointsCount, true)
                runnersRepository.updateRunnerData(it)?.let {updatedRunner ->
                    onRunnerChanged?.invoke(ResultOfTask.build { RunnerChange(updatedRunner, Change.UPDATE) })
                }
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String, type: RunnerType ): ResultOfTask<Exception, RunnerChange> {
        val runner = runnersRepository.getRunnerById(cardId, type) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
        val checkpointsCount = runnersRepository.getCheckpointsCount(runner.type)
        runner.addPassedCheckpoint(CheckpointResult(currentCheckpoint.id, currentCheckpoint.name, Date()), checkpointsCount)
        val updatedRunner = runnersRepository.updateRunnerData(runner) ?: return ResultOfTask.build { throw SaveRunnerDataException() }
        return ResultOfTask.build { RunnerChange(updatedRunner, Change.UPDATE) }
    }

    override suspend fun removeCheckpointForRunner(cardId: String, checkpointId: Int, type: RunnerType): ResultOfTask<Exception, RunnerChange> {
        val runner = runnersRepository.getRunnerById(cardId, type) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        runner.removeCheckpoint(checkpointId)
        val updatedRunner = runnersRepository.updateRunnerData(runner) ?: return ResultOfTask.build { throw SaveRunnerDataException() }
        return ResultOfTask.build { RunnerChange(updatedRunner, Change.UPDATE) }
    }

    override suspend fun getCheckpointCount(type: RunnerType): Int = runnersRepository.getCheckpointsCount(type)

    override suspend fun finishWork() {
        onRunnerChanged = null
        runnersRepository.finishWork()
    }
}