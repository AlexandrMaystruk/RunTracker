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
    fun getDistanceByRaceId(raceId: Long): List<DistanceTable>

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceById(distanceId: Long): List<DistanceTable>

    @Query("SELECT * FROM distances WHERE distanceId =:distanceId")
    fun getDistanceByIdWithRunners(distanceId: Long): List<DistanceWithRunners>

    @Query("DELETE FROM distances")
    fun deleteDistances()



    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: DistanceRunnerCrossRef)

}


