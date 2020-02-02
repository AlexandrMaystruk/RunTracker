package com.gmail.maystruks08.data.local

import androidx.room.*

@Dao
interface RunnerDAO {

    @Transaction
    suspend fun insertRunner(runner: RunnerTable, checkpoints: List<CheckpointTable>) {
        insert(runner)
        insertCheckpoints(checkpoints)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: RunnerTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoints(checkpoints: List<CheckpointTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<RunnerTable>)

    @Query("SELECT * FROM runners")
    suspend fun getRunners(): List<RunnerWithCheckpoints>

    @Update
    suspend fun update(menu: RunnerTable)

    @Update
    suspend fun update(menus: List<RunnerTable>)

    @Delete
    suspend fun delete(menu: RunnerTable)

    @Query("DELETE FROM runners WHERE number =:number ")
    suspend fun delete(number: Int)

    @Query("DELETE FROM runners")
    suspend fun dropTable()
}


