package com.gmail.maystruks08.data.local.dao

import androidx.room.*
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerWithResult
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface RunnerDao : BaseDao<RunnerTable> {


    /** INSERT */
    @Transaction
    suspend fun insertOrReplaceRunner(
        distanceId: Long,
        runner: RunnerTable,
        results: List<ResultTable>
    ) {
        insertOrReplace(runner)
        insertAllOrReplaceResults(results)
        insertOrReplaceJoin(DistanceRunnerCrossRef(distanceId, runner.runnerNumber))
    }

    @Transaction
    suspend fun insertRunner(distanceId: Long, runner: RunnerTable, results: List<ResultTable>) {
        insert(runner)
        insertAllResult(results)
        insertJoin(DistanceRunnerCrossRef(distanceId, runner.runnerNumber))
    }


    /** GET */

    @Transaction
    @Query("SELECT * FROM runners WHERE runners.runnerNumber =:runnerNumber")
    fun getRunnerWithResults(runnerNumber: Int): RunnerWithResult

    @Transaction
    @Query("SELECT * FROM runners WHERE runners.cardId =:cardUUID")
    fun getRunnerWithResults(cardUUID: String): RunnerWithResult


    @Query("SELECT * FROM runners WHERE runners.cardId =:cardUUID")
    fun getRunnerTable(cardUUID: String): RunnerTable


    @Transaction
    @Query("SELECT * FROM runners WHERE runners.runnerNumber =:runnerNumber")
    fun getRunnerResults(runnerNumber: Long): List<RunnerWithResult>

    @Transaction
    @Query("SELECT * FROM runners WHERE needToSync = 1")
    fun getNotUploadedRunners(): List<RunnerWithResult>


    @Transaction
    @Query("UPDATE runners SET cardId = :newCardUUID WHERE runnerNumber = :number")
    fun updateRunnerCardId(number: Int, newCardUUID: String)

    @Transaction
    @Query("SELECT * FROM runners WHERE runnerNumber =:runnerNumber AND needToSync = 1 ")
    suspend fun checkNeedToSync(runnerNumber: Long): RunnerTable?

    @Query("UPDATE runners SET needToSync = :needToSync WHERE runnerNumber = :runnerNumber")
    suspend fun markAsNeedToSync(runnerNumber: Long, needToSync: Boolean)


    @Query("DELETE FROM runners WHERE runnerNumber =:runnerNumber ")
    suspend fun delete(runnerNumber: Long): Int


    @Delete
    suspend fun deleteJoin(join: DistanceRunnerCrossRef): Int


    /** CHECKPOINTS */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplaceResults(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllResult(obj: List<ResultTable>)


    /** JOIN */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrReplaceJoin(join: DistanceRunnerCrossRef)

}


