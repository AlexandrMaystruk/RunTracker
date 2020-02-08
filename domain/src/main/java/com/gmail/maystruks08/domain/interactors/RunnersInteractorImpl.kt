package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository) :
    RunnersInteractor {

    override suspend fun bindGoogleDriveService(): ResultOfTask<Exception, String> =
         runnersRepository.bindGoogleDriveService()

    override suspend fun getAllRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> =
        runnersRepository.getAllRunners(type)

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) =
        runnersRepository.updateRunnersCache(type, onResult)

    override suspend fun addCurrentCheckpointToRunner(cardId: String): ResultOfTask<Exception, RunnerChange> {
        val runner = runnersRepository.getRunnerById(cardId) ?: return ResultOfTask.build { throw RunnerNotFoundException() }
        val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
        runner.markCheckpointAsPassed(currentCheckpoint)
        val updatedRunner = runnersRepository.updateRunnerData(runner) ?: return ResultOfTask.build { throw SaveRunnerDataException() }
        return ResultOfTask.build { RunnerChange(updatedRunner, Change.UPDATE) }
    }

    override suspend fun finishWork() {
        runnersRepository.finishWork()
    }
}