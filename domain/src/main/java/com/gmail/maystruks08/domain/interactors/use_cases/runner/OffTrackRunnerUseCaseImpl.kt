package com.gmail.maystruks08.domain.interactors.use_cases.runner

import com.gmail.maystruks08.domain.INFO
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import javax.inject.Inject

class OffTrackRunnerUseCaseImpl @Inject constructor(
    private val logHelper: LogHelper,
    private val runnersRepository: RunnersRepository
) : OffTrackRunnerUseCase {

    override suspend fun invoke(runnerNumber: String): IRunner {
        val runner = runnersRepository.getRunnerByNumber(runnerNumber) ?: throw RunnerNotFoundException()
        runner.markThatRunnerIsOffTrack()
        logHelper.log(INFO, "Runner ${runner.number} ${runner.fullName} is off track")
        runnersRepository.updateRunnerData(runner)
        val teamName = runner.currentTeamName
        if (!teamName.isNullOrEmpty()) {
            return runnersRepository.getTeam(teamName) ?: runner
        }
        return runner
    }

}