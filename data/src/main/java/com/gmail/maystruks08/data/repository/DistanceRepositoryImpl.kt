package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import com.gmail.maystruks08.data.mappers.toDistanceEntity
import com.gmail.maystruks08.data.mappers.toTable
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceStatistic
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.DistanceRepository
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DistanceRepositoryImpl @Inject constructor(
    private val firestoreApi: Api,
    private val distanceDAO: DistanceDAO,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences,
    private val gson: Gson,
    private val checkpointsRepository: CheckpointsRepository
) : DistanceRepository {

    override suspend fun observeDistanceDataFlow(raceId: String) {
        firestoreApi
            .subscribeToDistanceCollectionChange(raceId)
            .collect { distanceChangeList ->
                distanceChangeList.forEach {
                    val distanceWithRunnersIds = it.entity.toTable()
                    val canRewriteLocalCache = checkIsDataUploaded(distanceWithRunnersIds.first.distanceId)
                    if (canRewriteLocalCache) {
                        checkpointsRepository.getCheckpoints(raceId, it.entity.id)
                        when (it.modifierType) {
                            ModifierType.ADD -> insertDistance(distanceWithRunnersIds)
                            ModifierType.UPDATE -> updateDistance(distanceWithRunnersIds)
                            ModifierType.REMOVE -> deleteDistance(distanceWithRunnersIds)
                        }
                    }
                }
            }
    }

    override suspend fun getDistanceListFlow(raceId: String): Flow<List<Distance>> {
        return distanceDAO.getDistanceDistinctUntilChanged(raceId).map { distanceList ->
            distanceList.map { it.toDistanceEntity() }
        }
    }

    override suspend fun saveDistanceStatistic(raceId: String, distanceId: String, statistic: DistanceStatistic) {
        distanceDAO.updateDistanceStatistic(
            raceId = raceId,
            distanceId = distanceId,
            runnerCountInProgress = statistic.runnerCountInProgress,
            runnerCountOffTrack = statistic.runnerCountOffTrack,
            finisherCount = statistic.finisherCount
        )
    }

    private fun checkIsDataUploaded(distanceId: String): Boolean {
        //TODO implement
        return true
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
        val distanceTable = distance.first
        val distanceWithRunnerJoin = distance.second
        distanceDAO.getDistanceById(distanceTable.distanceId)
        distanceWithRunnerJoin.forEach {
            distanceDAO.deleteDistanceJoin(it.distanceId)
        }
//        Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
    }
}