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
    fun insertRunners(runnersData: List<Pair<RunnerTable,  List<ResultTable>>>) {
        runnersData.forEach {
            insert(it.first)
            insertAllResult(it.second)
        }
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
    fun getRunnersWithResults(type: Int): List<RunnerTableView> {
        val runners = getRunners(type)
        return runners.map { RunnerTableView(it, getRunnerResults(it.number)) }
    }

    @Query("SELECT * FROM checkpoints WHERE checkpointType =:type")
    fun getCheckpoints(type: Int): List<CheckpointTable>


    @Query("SELECT * FROM runners WHERE runners.cardId =:cardUUID")
    fun getRunner(cardUUID: String): RunnerTable

    @Transaction
    @Query("UPDATE runners SET cardId = :newCardUUID WHERE number = :number")
    fun updateRunnerCardId(number: Int, newCardUUID: String)

    @Transaction
    @Query("SELECT * FROM result LEFT JOIN runners ON result.runnerNumber = runners.number WHERE runners.number =:runnerNumber")
    fun getRunnerResults(runnerNumber: Int): List<ResultTable>

    @Transaction
    @Query("SELECT * FROM runners WHERE type =:type ")
    fun getRunners(type: Int): List<RunnerTable>

    @Transaction
    @Query("SELECT * FROM runners WHERE number =:runnerNumber AND needToSync = 1 ")
    fun checkNeedToSync(runnerNumber: Int): RunnerTable?


    @Query("UPDATE runners SET needToSync = :needToSync WHERE number = :runnerNumber")
    suspend fun markAsNeedToSync(runnerNumber: Int, needToSync: Boolean)

    @Query("DELETE FROM runners WHERE number =:runnerNumber ")
    suspend fun delete(runnerNumber: Int): Int

    @Transaction
    @Query("SELECT * FROM runners WHERE needToSync = 1")
    fun getNotUploadedRunners(): List<RunnerTable>

    @Transaction
    fun getNotUploadedRunnersWithResults(): List<RunnerTableView> {
        val runners = getNotUploadedRunners()
        return runners.map { RunnerTableView(it, getRunnerResults(it.number)) }
    }


    /**
     * CHECKPOINTS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplaceResults(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllResult(obj: List<ResultTable>)

}


