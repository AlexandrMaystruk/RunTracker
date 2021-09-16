package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toDistanceEntity
import com.gmail.maystruks08.data.mappers.toTable
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.repository.CheckpointsRepository
import com.gmail.maystruks08.domain.repository.DistanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DistanceRepositoryImpl @Inject constructor(
    private val firestoreApi: Api,
    private val distanceDAO: DistanceDAO,
    private val checkpointsRepository: CheckpointsRepository,
    private val configPreferences: ConfigPreferences,
) : DistanceRepository {

    override suspend fun observeDistanceDataFlow(raceId: String): Flow<Unit> {
       return firestoreApi
            .subscribeToDistanceCollectionChange(raceId)
            .map { distanceChangeList ->
                distanceChangeList.forEach {
                    val distanceWithRunnersIds = it.entity.toTable()
                    val canRewriteLocalCache = checkIsDataUploaded(distanceWithRunnersIds.first.distanceId)
                    if (canRewriteLocalCache) {
                        when (it.modifierType) {
                            ModifierType.ADD -> insertDistance(distanceWithRunnersIds)
                            ModifierType.UPDATE -> updateDistance(distanceWithRunnersIds)
                            ModifierType.REMOVE -> deleteDistance(distanceWithRunnersIds)
                        }
                        //need to call this for cache checkpoints
                        checkpointsRepository.getCheckpoints(it.entity.id)
                    }
                }
            }
    }

    override suspend fun getDistanceListFlow(raceId: String): Flow<List<Distance>> {
        return distanceDAO.getDistanceDistinctUntilChanged(raceId).map { distanceList ->
            distanceList.map { distanceWithCheckpoints ->
                val checkpoints = distanceWithCheckpoints.checkpoints.map { it.toCheckpoint() }.toMutableList()
                checkpoints.sortBy { it.getPosition() }
                distanceWithCheckpoints.distance.toDistanceEntity(checkpoints)
            }
        }
    }

    override suspend fun getLastSelectedRace(): Pair<String, String> {
        return configPreferences.getRaceId() to configPreferences.getRaceName()
    }

    override suspend fun updateDistanceName(distanceId: String, newName: String) {
        firestoreApi.updateDistanceName(distanceId, newName)
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
        distanceDAO.deleteDistance(distanceTable.distanceId)
        distanceWithRunnerJoin.forEach {
            distanceDAO.deleteDistanceJoin(it.distanceId)
        }
    }
}