package com.gmail.maystruks08.data.local

import androidx.room.*

@Dao
interface RunnerDao : BaseDao<RunnerTable> {

    @Transaction
    suspend fun insertOrReplaceRunner(runner: RunnerTable, checkpoints: List<CheckpointTable>) {
        insertOrReplace(runner)
        insertAllOrReplaceCheckpoint(checkpoints)
    }

    @Transaction
    suspend fun insertRunner(runner: RunnerTable, checkpoints: List<CheckpointTable>) {
        insert(runner)
        insertAllCheckpoints(checkpoints)
    }

    @Transaction
    suspend fun updateRunner(runner: RunnerTable, checkpoints: List<CheckpointTable>) {
        update(runner)
        checkpoints.forEach {
            update(it)
        }
    }

    @Query("SELECT * FROM runners")
    suspend fun getRunners(): List<RunnerWithCheckpoints>

    @Query("UPDATE runners SET needToSync = :needToSync WHERE id = :runnerId")
    suspend fun markAsNeedToSync(runnerId: String, needToSync: Boolean)

    @Query("DELETE FROM runners WHERE number =:number ")
    suspend fun delete(number: Int)

    @Query("DELETE FROM runners")
    suspend fun dropTable()


    /**
     * CHECKPOINTS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrReplaceCheckpoint(obj: List<CheckpointTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllCheckpoints(obj: List<CheckpointTable>)

    @Update
    suspend fun update(obj: CheckpointTable)

}


