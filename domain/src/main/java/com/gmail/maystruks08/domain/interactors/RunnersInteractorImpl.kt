package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class RunnersInteractorImpl @Inject constructor(private val runnersRepository: RunnersRepository) :
    RunnersInteractor {

    override suspend fun getAllRunners(): List<Runner> = runnersRepository.getAllRunners()

    override suspend fun getFilteredRunners(filter: String): List<Runner> {
        val pattern = ".*${filter.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
        return runnersRepository.getAllRunners().filter {
            pattern.containsMatchIn(it.number.toString().toLowerCase())
        }
    }

    override suspend fun addCurrentCheckpointToRunner(cardId: String): Runner? {
        return runnersRepository.getRunnerById(cardId)?.let { runner ->
            val currentCheckpoint = runnersRepository.getCurrentCheckpoint(runner.type)
            runner.markCheckpointAsPassed(currentCheckpoint)
            runnersRepository.updateRunnerData(runner)
        }
    }
}