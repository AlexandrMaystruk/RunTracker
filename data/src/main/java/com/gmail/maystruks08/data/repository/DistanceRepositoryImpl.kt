package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.cache.ApplicationCache
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
    private val applicationCache: ApplicationCache,
    private val networkUtil: NetworkUtil,
    private val configPreferences: ConfigPreferences,
    private val gson: Gson
) : DistanceRepository {

    @FlowPreview
    override suspend fun observeDistanceData(raceId: Long): Flow<Change<Distance>> {
        return firestoreApi
            .subscribeToDistanceCollectionChange(raceId.toString())
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

    private fun checkIsDataUploaded(distanceId: Long): Boolean {
        //TODO implement
        return true
    }

    override suspend fun getDistanceList(raceId: Long): List<Distance> {
        val distanceTableList = distanceDAO.getDistanceByRaceId(raceId)
        return distanceTableList.map { it.toDistanceEntity() }
    }


    private suspend fun insertDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
//        val runnerTable = runner.toRunnerTable(false)
//        val resultTables = runner.checkpoints.toCheckpointsResult(runner.number)
//        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
//        if (index != -1) {
//            runnerDao.updateRunner(runnerTable, resultTables)
//            runnersCache.getRunnerList(runner.type).removeAt(index)
//            runnersCache.getRunnerList(runner.type).add(index, runner)
//        } else {
//            runnerDao.insertOrReplaceRunner(runnerTable, resultTables)
//            runnersCache.getRunnerList(runner.type).add(runner)
//        }
    }

    private suspend fun updateDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
//        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
//        if (index != -1) {
//            runnerDao.updateRunner(runner.toRunnerTable(false), runner.checkpoints.toCheckpointsResult(runner.number))
//            runnersCache.getRunnerList(runner.type).removeAt(index)
//            runnersCache.getRunnerList(runner.type).add(index, runner)
//        }
    }

    private suspend fun deleteDistance(distance: Pair<DistanceTable, List<DistanceRunnerCrossRef>>) {
//        val count = runnerDao.delete(runner.number)
//        val isRemoved = runnersCache.getRunnerList(runner.type).removeAll { it.number == runner.number }
//        Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
    }
}