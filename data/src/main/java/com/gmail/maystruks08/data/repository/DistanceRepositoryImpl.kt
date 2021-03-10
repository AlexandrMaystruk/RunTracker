package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import com.gmail.maystruks08.data.mappers.*
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.repository.DistanceRepository
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DistanceRepositoryImpl @Inject constructor(
    private val firestoreApi: Api,
    private val distanceDAO: DistanceDAO,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences,
    private val gson: Gson
) : DistanceRepository {

    @FlowPreview
    override suspend fun observeDistanceData(raceId: String): Flow<Change<Distance>> {
        return firestoreApi
            .subscribeToDistanceCollectionChange(raceId)
            .flatMapConcat { distanceChangeList ->
                return@flatMapConcat channelFlow {
                    distanceChangeList.forEach {
                        val distanceWithRunnersIds = it.entity.toTable()
                        val distanceTable = distanceWithRunnersIds.first
                        val canRewriteLocalCache = checkIsDataUploaded(distanceWithRunnersIds.first.distanceId)
                        if (canRewriteLocalCache) {
                            when (it.modifierType) {
                                ModifierType.ADD -> insertDistance(distanceWithRunnersIds)
                                ModifierType.UPDATE -> updateDistance(distanceWithRunnersIds)
                                ModifierType.REMOVE -> deleteDistance(distanceWithRunnersIds)
                            }
                        }
                        val distanceWithoutRunners = distanceTable.toDistanceEntity()
                        offer(Change(distanceWithoutRunners, it.modifierType))
                    }
                }
            }
    }

    private fun checkIsDataUploaded(distanceId: String): Boolean {
        //TODO implement
        return true
    }

    override suspend fun getDistanceList(raceId: String): List<Distance> {
        val distanceTableList = distanceDAO.getDistanceByRaceId(raceId)
        return distanceTableList.map { it.toDistanceEntity() }
    }


    private fun insertDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
        val distanceTable = distance.first
        val distanceWithRunnerJoin = distance.second
        distanceDAO.insertOrReplace(distanceTable)
        distanceDAO.insertJoin(distanceWithRunnerJoin)
    }

    private fun updateDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
        deleteDistance(distance)
        insertDistance(distance)
    }

    private fun deleteDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
        //TODO implement
        val distanceTable = distance.first
        val distanceWithRunnerJoin = distance.second
        distanceDAO.getDistanceById(distanceTable.distanceId)
        distanceWithRunnerJoin.forEach {
            distanceDAO.deleteDistanceJoin(it.distanceId)
        }
//        Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
    }
}