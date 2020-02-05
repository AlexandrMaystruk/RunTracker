package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository) :
    RunnersInteractor {

    override suspend fun bindGoogleDriveService(): ResultOfTask<Exception, String>{
        return runnersRepository.bindGoogleDriveService()
    }

    override suspend fun getAllRunners(): ResultOfTask<Exception, List<Runner>> = runnersRepository.getAllRunners()

    override suspend fun updateRunnersCache(onResult: (ResultOfTask<Exception, List<Runner>>) -> Unit){
        runnersRepository.updateRunnersCache(onResult)
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String): Runner? {
        return runnersRepository.getRunnerById(cardId)?.let { runner ->
            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
            runner.markCheckpointAsPassed(currentCheckpoint)
            runnersRepository.updateRunnerData(runner)
        }
    }

    override suspend fun finishWork() {
        runnersRepository.finishWork()
    }
}