package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.tables.RaceTable
import com.gmail.maystruks08.data.local.entity.relation.RaceWithDistances
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceDAO : BaseDao<RaceTable> {

    @Query("SELECT * FROM race_table")
    fun getRaceList(): Flow<List<RaceWithDistances>>

    @Query("SELECT * FROM race_table WHERE name LIKE :query")
    fun getRaceList(query: String): List<RaceWithDistances>

    @Query("SELECT * FROM race_table WHERE id =:raceId")
    fun getRace(raceId: String): RaceWithDistances

    @Query("DELETE FROM race_table WHERE id =:raceId")
    fun deleteAllRaceById(raceId: String)

    @Query("DELETE FROM race_table")
    fun deleteAllRaces()

}
