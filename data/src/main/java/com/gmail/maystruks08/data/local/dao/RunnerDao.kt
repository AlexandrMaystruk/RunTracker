package com.gmail.maystruks08.data.local.dao

import androidx.room.*
import com.gmail.maystruks08.data.local.entity.CheckpointTable
import com.gmail.maystruks08.data.local.entity.ResultTable
import com.gmail.maystruks08.data.local.entity.RunnerTable
import com.gmail.maystruks08.data.local.pojo.RunnerTableView

@Dao
interface RunnerDao :
    BaseDao<RunnerTable> {

    @Transaction
    fun insertOrReplaceRunner(runner: RunnerTable, results: List<ResultTable>) {
        insertOrReplace(runner)
        insertAllOrReplaceCheckpoint(results)
    }

    @Transaction
    fun insertRunner(runner: RunnerTable, results: List<ResultTable>) {
        insert(runner)
        insertAllCheckpoints(results)
    }

    @Transaction
    suspend fun updateRunner(runner: RunnerTable, results: List<ResultTable>) {
        update(runner)
        results.forEach { update(it) }
    }

    @Transaction
    fun getRunnerWithResults(id: String): RunnerTableView {
       return RunnerTableView(getRunner(id), getRunnerResults(id))
    }

    @Transaction
    fun getRunnersWithResults(type: Int): List<RunnerTableView> {
        val runners = getRunners(type)
        return runners.map { RunnerTableView(it, getRunnerResults(it.id)) }
    }

    @Query("SELECT * FROM checkpoints WHERE checkpointType =:type")
    fun getCheckpoints(type: Int): List<CheckpointTable>


    @Query("SELECT * FROM runners WHERE runners.id =:id")
    fun getRunner(id: String): RunnerTable

    @Transaction
    @Query("SELECT * FROM result LEFT JOIN runners ON result.runnerId = runners.id WHERE runners.id =:id")
    fun getRunnerResults(id: String): List<ResultTable>

    @Transaction
    @Query("SELECT * FROM runners WHERE type =:type ")
    fun getRunners(type: Int): List<RunnerTable>


    @Query("UPDATE runners SET needToSync = :needToSync WHERE id = :runnerId")
    suspend fun markAsNeedToSync(runnerId: String, needToSync: Boolean)

    @Query("DELETE FROM runners WHERE id =:runnerId ")
    suspend fun delete(runnerId: String): Int



    /**
     * CHECKPOINTS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplaceCheckpoint(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllCheckpoints(obj: List<ResultTable>)

    @Update
    fun update(obj: ResultTable)

}


