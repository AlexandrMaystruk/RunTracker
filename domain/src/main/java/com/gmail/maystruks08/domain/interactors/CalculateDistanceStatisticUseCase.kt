package com.gmail.maystruks08.domain.interactors

interface CalculateDistanceStatisticUseCase {

    suspend fun invoke(raceId: String, distanceId: String)

}