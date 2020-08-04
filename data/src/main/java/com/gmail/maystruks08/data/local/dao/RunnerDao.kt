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
        insertAllOrReplaceResults(results)
    }

    @Transaction
    fun insertRunner(runner: RunnerTable, results: List<ResultTable>) {
        insert(runner)
        insertAllResult(results)
    }

    @Transaction
    suspend fun updateRunner(runner: RunnerTable, results: List<ResultTable>) {
        insertOrReplace(runner)
        insertAllOrReplaceResults(results)
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

    @Transaction
    @Query("SELECT * FROM runners WHERE id =:id AND needToSync = 1 ")
    fun checkNeedToSync(id: String): RunnerTable?


    @Query("UPDATE runners SET needToSync = :needToSync WHERE id = :runnerId")
    suspend fun markAsNeedToSync(runnerId: String, needToSync: Boolean)

    @Query("DELETE FROM runners WHERE id =:runnerId ")
    suspend fun delete(runnerId: String): Int

    @Transaction
    @Query("SELECT * FROM runners WHERE needToSync = 1")
    fun getNotUploadedRunners(): List<RunnerTable>

    @Transaction
    fun getNotUploadedRunnersWithResults(): List<RunnerTableView> {
        val runners = getNotUploadedRunners()
        return runners.map { RunnerTableView(it, getRunnerResults(it.id)) }
    }


    /**
     * CHECKPOINTS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplaceResults(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllResult(obj: List<ResultTable>)

}


