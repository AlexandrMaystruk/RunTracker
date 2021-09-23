package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnerInteractorImpl @Inject constructor(
    private val runnersRepository: RegisterNewRunnersRepository
) : RegisterNewRunnerUseCase {

    override suspend fun invoke(
        raceId: String,
        distanceId: String,
        distanceType: DistanceType,
        registerInputData: List<RegisterNewRunnerUseCase.RegisterInputData>
    ): TaskResult<Exception, Unit> {
        return TaskResult.build {
            val runners = registerInputData.map {
                Runner(
                    cardId = null,
                    fullName = it.fullName,
                    shortName = it.shortName,
                    phone = it.phone,
                    number = it.runnerNumber,
                    sex = it.runnerSex,
                    city = it.city,
                    dateOfBirthday = it.dateOfBirthday,
                    actualDistanceId = distanceId,
                    actualRaceId = raceId,
                    currentCheckpoints = mutableListOf(),
                    offTrackDistance = null,
                    currentTeamName = it.teamName,
                    result = null
                )
            }
            runnersRepository.saveNewRunners(raceId, distanceId, runners)
        }
    }
}