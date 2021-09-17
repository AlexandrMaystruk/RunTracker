package com.gmail.maystruks08.data.local.dao

import androidx.room.*
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.DistanceStatisticTable
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithCheckpoints
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithRunners
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Dao
interface DistanceDAO : BaseDao<DistanceTable> {

    @Query("SELECT * FROM distances WHERE raceId =:raceId ORDER BY name")
    fun getDistanceByRaceIdFlow(raceId: String): Flow<List<DistanceTable>>

    fun getDistanceDistinctUntilChanged(raceId: String) = getDistanceByRaceIdFlow(raceId).distinctUntilChanged().map { distanceTables ->
        distanceTables.map {
            DistanceWithCheckpoints(it, getDistanceCheckpoints(it.distanceId))
        }
    }

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceById(distanceId: String): DistanceTable

    @Query("SELECT type FROM distances WHERE distanceId =:distanceId")
    fun getDistanceTypeById(distanceId: String): String

    @Transaction
    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceWithCheckpoints(distanceId: String): DistanceWithCheckpoints

    @Query("SELECT distanceId FROM distances ORDER BY name LIMIT 1")
    fun getFirstDistanceId(): String

    @Transaction
    @Query("SELECT * FROM distances WHERE distanceId =:distanceId AND raceId =:raceId")
    fun getDistanceByIdWithRunners(raceId: String, distanceId: String): DistanceWithRunners


    @Query("SELECT runnerNumber FROM distance_runner_cross_ref LE WHERE distanceId =:distanceId")
    fun getDistanceRunnersIds(distanceId: String): List<String>

    @Query("SELECT * FROM checkpoints WHERE distanceId =:distanceId")
    fun getDistanceCheckpoints(distanceId: String): List<CheckpointTable>

    @Query("DELETE FROM distances WHERE distanceId =:distanceId")
    fun deleteDistance(distanceId: String)

    @Query("DELETE FROM distances")
    fun deleteDistances()



    @Query("SELECT COUNT(*) FROM runners WHERE actualRaceId =:raceId AND actualDistanceId =:distanceId;")
    fun getRunnersCount(
        raceId: String,
        distanceId: String
    ): Int

    @Query("SELECT COUNT(*) FROM runners  WHERE actualRaceId =:raceId AND actualDistanceId =:distanceId AND isOffTrackMapJson LIKE '%' || :distanceId || '%';")
    fun getRunnerCountOffTrack(
        raceId: String,
        distanceId: String
    ): Int

    @Query("SELECT COUNT(*) FROM runners WHERE actualRaceId =:raceId AND actualDistanceId =:distanceId AND isOffTrackMapJson NOT LIKE '%' || :distanceId || '%' AND (SELECT COUNT(*) FROM checkpoints WHERE checkpoints.distanceId =:distanceId) = (SELECT COUNT(*) FROM result WHERE runners.runnerNumber = result.runnerNumber )")
    fun getFinisherCount(
        raceId: String,
        distanceId: String
    ): Int


    @Query("SELECT runnerCountInProgress, runnerCountOffTrack, finisherCount FROM distances WHERE raceId =:raceId AND distanceId =:distanceId")
    fun getDistanceStatistic(
        raceId: String,
        distanceId: String
    ): DistanceStatisticTable?

    @Query("UPDATE distances SET runnerCountInProgress =:runnerCountInProgress, runnerCountOffTrack =:runnerCountOffTrack, finisherCount =:finisherCount WHERE distanceId =:distanceId AND raceId =:raceId")
    fun updateDistanceStatistic(
        raceId: String,
        distanceId: String,
        runnerCountInProgress: Int,
        runnerCountOffTrack: Int,
        finisherCount: Int
    )


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: List<DistanceRunnerCrossRef>)

    @Query("DELETE FROM distance_runner_cross_ref WHERE distanceId =:distanceId")
    fun deleteDistanceJoin(distanceId: String)

}


