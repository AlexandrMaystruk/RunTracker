package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.DistanceWithRunners
import com.gmail.maystruks08.data.local.entity.tables.DistanceTable

@Dao
interface DistanceDAO : BaseDao<DistanceTable> {

    @Query("SELECT * FROM distances WHERE raceId =:raceId")
    fun getDistanceByRaceId(raceId: String): List<DistanceTable>

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceById(distanceId: String): List<DistanceTable>

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId AND raceId =:raceId")
    fun getDistanceByIdWithRunners(raceId: String, distanceId: String): DistanceWithRunners


    @Query("SELECT runnerNumber FROM DistanceRunnerCrossRef LE WHERE distanceId =:distanceId")
    fun getDistanceRunnersIds(distanceId: String): List<Long>

    @Query("DELETE FROM distances")
    fun deleteDistances()



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: List<DistanceRunnerCrossRef>)

    @Query("DELETE FROM DistanceRunnerCrossRef WHERE distanceId =:distanceId")
    fun deleteDistanceJoin(distanceId: String)

}


