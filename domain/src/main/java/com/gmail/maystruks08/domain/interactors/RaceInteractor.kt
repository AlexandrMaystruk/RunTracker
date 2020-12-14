package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.TaskResult

interface RaceInteractor {

    suspend fun getRaceList(): TaskResult<Exception, List<Race>>

    suspend fun saveLastSelectedRaceId(raceId: Long)

}