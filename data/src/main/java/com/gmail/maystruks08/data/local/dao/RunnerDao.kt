package com.gmail.maystruks08.data.local.dao

import androidx.room.*
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerResultCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerWithResult
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable
import com.gmail.maystruks08.data.local.entity.tables.TeamNameTable

@Dao
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
interface RunnerDao : BaseDao<RunnerTable> {


    /** INSERT */
    @Transaction
    suspend fun insertOrReplaceRunner(
        runner: RunnerTable,
        results: List<ResultTable>,
        teams: List<TeamNameTable>,
        runnerResultCrossRefTables: List<RunnerResultCrossRef>,
        distanceRunnerCrossRefTables: List<DistanceRunnerCrossRef>
    ) {
        insertOrReplace(runner)
        insertAllOrReplaceResults(results)
        teams.forEach { insertOrReplaceRunnerTeams(it) }
        runnerResultCrossRefTables.forEach { insertOrReplaceRunnerResultJoin(it) }
        distanceRunnerCrossRefTables.forEach { insertOrReplaceDistanceRunnerJoin(it) }
    }


    @Transaction
    suspend fun insertOrReplaceRunners(
        runners: List<RunnerTable>,
        results: List<ResultTable>,
        teams: List<TeamNameTable>,
        runnerResultCrossRefTables: List<RunnerResultCrossRef>,
        distanceRunnerCrossRefTables: List<DistanceRunnerCrossRef>
    ) {
        insertAllOrReplace(runners)
        insertAllOrReplaceResults(results)
        teams.forEach { insertOrReplaceRunnerTeams(it) }
        runnerResultCrossRefTables.forEach { insertOrReplaceRunnerResultJoin(it) }
        distanceRunnerCrossRefTables.forEach { insertOrReplaceDistanceRunnerJoin(it) }
    }


    /** GET */

    @Transaction
    @Query("SELECT * FROM runners INNER JOIN distance_runner_cross_ref ON runners.runnerNumber == distance_runner_cross_ref.runnerNumber WHERE actualRaceId =:raceId AND distanceId =:distanceId;")
    fun getRunnersWithResults(raceId: String, distanceId: String): List<RunnerWithResult>

    @Transaction
    @Query("SELECT * FROM runners INNER JOIN distance_runner_cross_ref ON runners.runnerNumber == distance_runner_cross_ref.runnerNumber WHERE actualRaceId =:raceId AND distanceId =:distanceId AND runners.runnerNumber LIKE '%'||:query||'%';")
    fun getRunnerWithResultsQuery(
        raceId: String,
        distanceId: String,
        query: String
    ): List<RunnerWithResult>


    @Transaction
    @Query("SELECT * FROM runners INNER JOIN distance_runner_cross_ref ON runners.runnerNumber == distance_runner_cross_ref.runnerNumber INNER JOIN teams ON runners.runnerNumber == teams.runnerId WHERE runners.actualRaceId =:raceId AND runners.actualDistanceId =:distanceId AND teams.name IS NOT NULL;")
    fun getTeamRunnersWithResults(raceId: String, distanceId: String): List<RunnerWithResult>

    @Transaction
    @Query("SELECT * FROM runners INNER JOIN distance_runner_cross_ref ON runners.runnerNumber == distance_runner_cross_ref.runnerNumber INNER JOIN teams ON runners.runnerNumber == teams.runnerId WHERE teams.name =:teamName;")
    fun getTeamRunnersWithResultsByName(teamName: String): List<RunnerWithResult>


    @Transaction
    @Query("SELECT * FROM runners WHERE runners.runnerNumber =:runnerNumber")
    fun getRunnerWithResultsByCard(runnerNumber: Int): RunnerWithResult

    @Transaction
    @Query("SELECT * FROM runners WHERE runners.cardId =:cardUUID")
    fun getRunnerWithResultsByCard(cardUUID: String): RunnerWithResult?


    @Query("SELECT * FROM runners WHERE runners.cardId =:cardUUID")
    fun getRunnerTable(cardUUID: String): RunnerTable


    @Transaction
    @Query("SELECT DISTINCT distanceId FROM distance_runner_cross_ref WHERE runnerNumber =:runnerNumber ")
    fun getRunnerDistanceIds(runnerNumber: String): List<String>

    @Transaction
    @Query("SELECT DISTINCT id FROM race_table INNER JOIN distances ON race_table.id == distances.raceId INNER JOIN distance_runner_cross_ref ON distances.distanceId == distance_runner_cross_ref.distanceId WHERE runnerNumber =:runnerNumber")
    fun getRunnerRaceIds(runnerNumber: String): List<String>


    @Transaction
    @Query("SELECT * FROM runners WHERE runners.runnerNumber =:runnerNumber LIMIT 1")
    fun getRunnerWithResultsByNumber(runnerNumber: String): RunnerWithResult?

    @Transaction
    @Query("SELECT * FROM runners WHERE runners.cardId =:cardId LIMIT 1")
    fun getRunnerWithResultsByCardId(cardId: String): RunnerWithResult?

    @Transaction
    @Query("SELECT * FROM runners WHERE needToSync = 1")
    fun getNotUploadedRunners(): List<RunnerWithResult>


    @Transaction
    @Query("UPDATE runners SET cardId = :newCardUUID WHERE runnerNumber = :number")
    fun updateRunnerCardId(number: Int, newCardUUID: String)

    @Transaction
    @Query("SELECT * FROM runners WHERE runnerNumber =:runnerNumber AND needToSync = 1 ")
    suspend fun checkNeedToSync(runnerNumber: String): RunnerTable?

    @Query("UPDATE runners SET needToSync = :needToSync WHERE runnerNumber = :runnerNumber")
    suspend fun markAsNeedToSync(runnerNumber: String, needToSync: Boolean)


    @Query("DELETE FROM runners WHERE runnerNumber =:runnerNumber ")
    suspend fun delete(runnerNumber: String): Int

    @Query("DELETE FROM runners WHERE runnerNumber IN (:runnerNumbers)")
    suspend fun delete(runnerNumbers: List<String>): Int


    @Delete
    suspend fun deleteJoin(join: DistanceRunnerCrossRef): Int


    /** CHECKPOINTS */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplaceResults(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllResult(obj: List<ResultTable>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceRunnerTeams(table: TeamNameTable)


    /** JOIN */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDistanceRunnerJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDistanceRunnerJoin(join: DistanceRunnerCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceDistanceRunnerJoin(join: List<DistanceRunnerCrossRef>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceRunnerResultJoin(join: RunnerResultCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceRunnerResultJoin(join: List<RunnerResultCrossRef>)

}


