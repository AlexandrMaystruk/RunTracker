package com.gmail.maystruks08.domain.interactors.use_cases

interface CalculateDistanceStatisticUseCase {

    suspend fun invoke(distanceId: String?)

}