package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.runner.Runner

interface RegisterNewRunnersRepository {

    suspend fun saveNewRunners(raceId: String, distanceId: String, runners: List<Runner>)

}