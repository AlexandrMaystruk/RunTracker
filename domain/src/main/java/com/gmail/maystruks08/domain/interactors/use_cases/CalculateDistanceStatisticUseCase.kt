package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.DistanceStatistic

interface CalculateDistanceStatisticUseCase {

    suspend fun invoke(distanceId: String?): DistanceStatistic?

}