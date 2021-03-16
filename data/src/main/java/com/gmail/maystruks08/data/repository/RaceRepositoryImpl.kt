package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.RaceDAO
import com.gmail.maystruks08.data.mappers.toFirestoreRace
import com.gmail.maystruks08.data.mappers.toRaceEntity
import com.gmail.maystruks08.data.mappers.toRaceTable
import com.gmail.maystruks08.data.mappers.toTable
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.repository.RaceRepository
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class RaceRepositoryImpl @Inject constructor(
    private val api: Api,
    private val raceDAO: RaceDAO,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences,
    private val gson: Gson
) : RaceRepository {

    override suspend fun subscribeToUpdates() {
        api
            .subscribeToRaceCollectionChange()
            .collect { list ->
                list.forEach {
                    //TODO resolve conflict with local database
                    when (it.modifierType) {
                        ModifierType.ADD -> {
                            val raceTable = it.entity.toTable(gson)
                            raceDAO.insertOrReplace(raceTable)
                        }
                        ModifierType.UPDATE -> {
                            val raceTable = it.entity.toTable(gson)
                            raceDAO.insertOrReplace(raceTable)
                        }
                        ModifierType.REMOVE -> raceDAO.deleteAllRaceById(it.entity.id)
                    }
                }
            }
    }

    override suspend fun getRaceList(): Flow<List<Race>> {
        return raceDAO.getRaceList().map { raceWithDistances ->
            raceWithDistances.map { it.toRaceEntity(gson) }
        }
    }

    override suspend fun getRaceList(query: String): List<Race> {
        return raceDAO
            .getRaceList("'%$query%'")
            .map { it.toRaceEntity(gson) }
    }

    override suspend fun saveRace(race: Race) {
        raceDAO.insert(race.toRaceTable(gson))
        if (networkUtil.isOnline()) {
            api.saveRace(race.toFirestoreRace(gson))
        }
    }

    override suspend fun saveLastSelectedRaceId(raceId: String, raceName: String) {
        configPreferences.saveRaceId(raceId)
        configPreferences.saveRaceName(raceName)
    }

    override suspend fun getLastSelectedRace(): Pair<String, String> {
        return configPreferences.getRaceId() to configPreferences.getRaceName()
    }
}