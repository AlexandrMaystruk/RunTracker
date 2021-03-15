package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithRunners
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface DistanceDAO : BaseDao<DistanceTable> {

    @Query("SELECT * FROM distances WHERE raceId =:raceId ORDER BY name")
    fun getDistanceByRaceIdFlow(raceId: String): Flow<List<DistanceTable>>

    fun getDistanceDistinctUntilChanged(raceId: String) = getDistanceByRaceIdFlow(raceId).distinctUntilChanged()

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceById(distanceId: String): DistanceTable

    @Query("SELECT distanceId FROM distances ORDER BY name LIMIT 1")
    fun getFirstDistanceId(): String

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId AND raceId =:raceId")
    fun getDistanceByIdWithRunners(raceId: String, distanceId: String): DistanceWithRunners


    @Query("SELECT runnerNumber FROM distance_runner_cross_ref LE WHERE distanceId =:distanceId")
    fun getDistanceRunnersIds(distanceId: String): List<Long>

    @Query("DELETE FROM distances")
    fun deleteDistances()



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: List<DistanceRunnerCrossRef>)

    @Query("DELETE FROM distance_runner_cross_ref WHERE distanceId =:distanceId")
    fun deleteDistanceJoin(distanceId: String)

}


