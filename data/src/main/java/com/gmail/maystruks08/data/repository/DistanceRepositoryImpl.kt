package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.ApplicationCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RaceDAO
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.repository.DistanceRepository
import com.gmail.maystruks08.domain.repository.RaceRepository
import java.util.*
import javax.inject.Inject

class DistanceRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val distanceDAO: DistanceDAO,
    private val applicationCache: ApplicationCache,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences
) : DistanceRepository {

    override suspend fun getDistanceList(): List<Distance> {
        return mutableListOf(
            Distance(0, "Normal", 0, Date(), mutableListOf(), mutableSetOf()),
            Distance(1, "Iron", 1, Date(), mutableListOf(), mutableSetOf()),
            Distance(2, "Walking", 2, Date(), mutableListOf(), mutableSetOf()),
            Distance(2, "Walking 2", 2, Date(), mutableListOf(), mutableSetOf())
        )
    }
}