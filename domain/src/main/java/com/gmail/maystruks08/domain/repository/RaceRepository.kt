package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Race

interface RaceRepository {

    suspend fun getRaceList(): List<Race>

    suspend fun saveLastSelectedRaceId(raceId: Long)

}