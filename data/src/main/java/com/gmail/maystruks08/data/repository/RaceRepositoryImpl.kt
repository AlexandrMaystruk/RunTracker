package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.RaceDAO
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.repository.RaceRepository
import java.util.*
import javax.inject.Inject

class RaceRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val checkpointDAO: RaceDAO,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences
) : RaceRepository {

    override suspend fun getRaceList(): List<Race> {
        return listOf(
            Race(0, "Одесская сотка 2020", Date(), true, 0, mutableListOf()),
            Race(1, "Одесская сотка 2021", Date(), true, 0, mutableListOf()),
            Race(2, "Морозный полдень", Date(), true, 0, mutableListOf())
        )
    }

    override suspend fun saveLastSelectedRaceId(raceId: Long) {
        configPreferences.saveRaceId(raceId)
    }

    override suspend fun getLastSelectedRaceId(): Long {
        return configPreferences.getRaceId()
    }
}