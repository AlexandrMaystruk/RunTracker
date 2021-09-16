package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteException
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerResultCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerWithResult
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.local.entity.tables.RunnerTable
import com.gmail.maystruks08.data.local.entity.tables.TeamNameTable
import com.gmail.maystruks08.data.mappers.*
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.clearAndAddAll
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class RunnersRepositoryImpl @Inject constructor(
    private val networkUtil: NetworkUtil,
    private val api: Api,
    private val distanceDAO: DistanceDAO,
    private val runnerDao: RunnerDao,
    private val configPreferences: ConfigPreferences,
    private val gson: Gson
) : RunnersRepository, RunnerDataChangeListener {

    override suspend fun updateRunnerData(runner: Runner): Runner {
        try {
            Timber.i("Saving runner: ${runner.number} ${runner.fullName}")
            updateRunner(runner)
        } catch (e: SQLiteException) {
            Timber.e(e, "Saving runner ${runner.number} ${runner.fullName} to room error")
            throw SaveRunnerDataException(runner.fullName)
        }
        if (networkUtil.isOnline()) {
            try {
                runnerDao.markAsNeedToSync(runner.number, false)
                api.saveRunner(runner.toFirestoreRunner())
            } catch (e: FirebaseFirestoreException) {
                Timber.e(e, "Saving runner ${runner.number} data to firestore error")
                runnerDao.markAsNeedToSync(runner.number, true)
                throw SyncWithServerException()
            }
        }
        return runner
    }

    override suspend fun getRaceId(): String {
        return configPreferences.getRaceId()
    }

    override suspend fun observeRunnerData(raceId: String) {
        api
            .subscribeToRunnerCollectionChange(raceId)
            .flowOn(Dispatchers.IO)
            .collect { runnerChangeList ->
                when (runnerChangeList.type) {
                    ModifierType.UPDATE -> runnerChangeList.runners.updateRunnersTable()
                    ModifierType.ADD -> runnerChangeList.runners.insert()
                    ModifierType.REMOVE -> runnerChangeList.runners.deleteRunnersTables()
                }
            }
    }

    override suspend fun getRunners(
        distanceId: String,
        query: String,
        onlyFinishers: Boolean
    ): List<Runner> {
        val actualRaceId = getRaceId()
        return runnerDao.getRunnerWithResultsQuery(actualRaceId, distanceId, query)
            .mapNotNull { runnerWithResult ->
                when {
                    !onlyFinishers -> {
                        runnerWithResult.runnerTable.toRunner(gson).apply {
                            this.checkpoints[distanceId] = runnerWithResult.getCheckpoints()
                        }
                    }
                    onlyFinishers -> {
                        val checkpoints = runnerWithResult.getCheckpoints(true)
                        return@mapNotNull if (checkpoints.isNotEmpty()) runnerWithResult.runnerTable.toRunner(
                            gson
                        ).apply {
                            addCheckpoints(distanceId, checkpoints)
                        }
                        else null
                    }
                    else -> return@mapNotNull null
                }
            }
    }

    override suspend fun getRunnersFlow(
        distanceId: String,
        onlyFinishers: Boolean,
        query: String?
    ): Flow<List<Runner>> {
        val actualDistanceId = if (distanceId == DEF_STRING_VALUE) distanceDAO.getFirstDistanceId() else distanceId
        val actualRaceId = getRaceId()
        if (query.isNullOrEmpty()) {
            return flow {
                val runnersWithResults = runnerDao.getRunnerWithResults(actualRaceId, actualDistanceId)
                val runners = runnersWithResults.mapNotNull {
                    when {
                        !onlyFinishers -> {
                            it.runnerTable.toRunner(gson)
                                .apply {
                                    addCheckpoints(actualDistanceId, it.getCheckpoints())
                                }
                        }
                        onlyFinishers -> {
                            val checkpoints = it.getCheckpoints(onlyFinishers = true)
                            return@mapNotNull if (checkpoints.isNotEmpty()) it.runnerTable.toRunner(gson).apply { addCheckpoints(actualDistanceId, checkpoints) }
                            else null
                        }
                        else -> return@mapNotNull null
                    }
                }
                emit(runners)
            }
        }

        return flow {
            val runnersWithResults = runnerDao.getRunnerWithResultsQuery(actualRaceId, actualDistanceId, query)
            val runners = runnersWithResults.mapNotNull {
                when {
                    !onlyFinishers -> {
                        it.runnerTable.toRunner(gson).apply {
                            addCheckpoints(actualDistanceId, it.getCheckpoints())
                        }
                    }
                    onlyFinishers -> {
                        val checkpoints = it.getCheckpoints(onlyFinishers = true)
                        return@mapNotNull if (checkpoints.isNotEmpty()) it.runnerTable.toRunner(gson)
                            .apply { addCheckpoints(actualDistanceId, checkpoints) }
                        else null
                    }
                    else -> return@mapNotNull null
                }
            }
            emit(runners)
        }
    }

    override suspend fun getTeamRunnersFlow(
        distanceId: String,
        onlyFinishers: Boolean
    ): Flow<List<Team>> {
        val actualRaceId = getRaceId()
        val actualDistanceId = if (distanceId == DEF_STRING_VALUE) distanceDAO.getFirstDistanceId() else distanceId
        return runnerDao
            .getRunnerWithResultsFlow(actualRaceId, actualDistanceId)
            .distinctUntilChanged()
            .map { runnersWithResults ->
                runnersWithResults.groupBy { it.team?.name }.mapNotNull { entry ->
                    val teamName = entry.key ?: return@mapNotNull null
                    Team(teamName, entry.value.map { it.toRunner() })
                }
            }
    }

    override suspend fun getRunnerByCardId(cardId: String): Runner? {
        val runnerWithResultsTable = runnerDao.getRunnerWithResultsByCardId(cardId)
        return runnerWithResultsTable?.toRunner()
    }

    override suspend fun getRunnerByNumber(runnerNumber: String): Runner? {
        return runnerDao.getRunnerWithResultsByNumber(runnerNumber)?.toRunner()
    }

    private fun RunnerWithResult.toRunner(): Runner {
        return runnerTable.toRunner(gson).apply {
            this.raceIds.clearAndAddAll(runnerDao.getRunnerRaceIds(number))
            this.distanceIds.clearAndAddAll(runnerDao.getRunnerDistanceIds(number))
            this.checkpoints[actualDistanceId] = getCheckpoints()
        }
    }

    private fun RunnerWithResult.getCheckpoints(onlyFinishers: Boolean = false): MutableList<Checkpoint> {
        val distanceWithCheckpoints = distanceDAO.getDistanceWithCheckpoints(runnerTable.actualDistanceId)
        val checkpointResult = results.distinct()
        if (onlyFinishers && checkpointResult.size != distanceWithCheckpoints.checkpoints.size) return mutableListOf()
        val result =  distanceWithCheckpoints.checkpoints.map { checkpointTable ->
            val runnerResults = checkpointResult.firstOrNull { it.checkpointId == checkpointTable.checkpointId }
            val checkpoint = CheckpointImpl(
                checkpointTable.checkpointId,
                checkpointTable.distanceId,
                checkpointTable.name,
                checkpointTable.position
            )
            if (runnerResults == null) checkpoint
            else CheckpointResultIml(
                checkpoint,
                runnerResults.time,
                runnerResults.hasPrevious
            )
        }.toMutableList()
        result.sortBy { it.getPosition() }
        return result
    }

    override suspend fun getRunnerTeamMembers(
        currentRunnerNumber: String,
        teamName: String
    ): List<Runner>? {
        //TODO
        return null
    }

    override suspend fun getLastSavedRace(): TaskResult<Exception, Pair<String, String>> {
        return TaskResult.build {
            val lastSelectedRaceId = configPreferences.getRaceId()
            val lastSelectedRaceName = configPreferences.getRaceName()
            if (lastSelectedRaceId == DEF_STRING_VALUE) {
                throw Exception("First start. Race not selected yet")
            } else {
                lastSelectedRaceId to lastSelectedRaceName
            }
        }
    }


    private suspend fun checkIsDataUploaded(runnerNumber: String): Boolean {
        return runnerDao.checkNeedToSync(runnerNumber) == null
    }


    private suspend fun List<RunnerPojo>.insert() {
        val runners = mutableListOf<RunnerTable>()
        val resultTables = mutableListOf<ResultTable>()
        val runnerResultCrossRefTables = mutableListOf<RunnerResultCrossRef>()
        val distanceRunnerCrossRefTables = mutableListOf<DistanceRunnerCrossRef>()
        val teamNameTables = mutableListOf<TeamNameTable>()

        forEach { runnerPojo ->
            val runner = runnerPojo.fromFirestoreRunner()
            runner.toRunnerTable(gson, false).also { runners.add(it) }
            runner.distanceIds.map { DistanceRunnerCrossRef(it, runner.number) }
                .also { distanceRunnerCrossRefTables.addAll(it) }
            runner.teamNames.mapNotNull {
                val teamName = it.value ?: return@mapNotNull null
                TeamNameTable(
                    distanceId = it.key,
                    runnerId = runner.number,
                    name = teamName
                )
            }.also { teamNameTables.addAll(it) }

            runner.checkpoints.forEach { (_, checkpoints) ->
                checkpoints.forEach {
                    if (it is CheckpointResultIml) {
                        val result = it.toResultTable(runner.number)
                        val runnerResult = RunnerResultCrossRef(runner.number, it.getId())
                        resultTables.add(result)
                        runnerResultCrossRefTables.add(runnerResult)
                    }
                }
            }
        }
        runnerDao.insertOrReplaceRunners(
            runners,
            resultTables,
            teamNameTables,
            runnerResultCrossRefTables,
            distanceRunnerCrossRefTables
        )

    }

    private suspend fun List<RunnerPojo>.updateRunnersTable() {
        deleteRunnersTables()
        insert()
    }

    private suspend fun List<RunnerPojo>.deleteRunnersTables() {
        runnerDao.delete(map { it.number })
    }

    private suspend fun insertRunnersTables(runner: Runner) {
        val runnerTable = runner.toRunnerTable(gson, false)
        val resultTables = mutableListOf<ResultTable>()
        val runnerResultCrossRefTables = mutableListOf<RunnerResultCrossRef>()
        val distanceRunnerCrossRefTables =
            runner.distanceIds.map { DistanceRunnerCrossRef(it, runner.number) }
        val teamNameTables = runner.teamNames.mapNotNull {
            val teamName = it.value ?: return@mapNotNull null
            TeamNameTable(
                distanceId = it.key,
                runnerId = runner.number,
                name = teamName
            )
        }

        runner.checkpoints.forEach { (_, checkpoints) ->
            checkpoints.forEach {
                if (it is CheckpointResultIml) {
                    val result = it.toResultTable(runner.number)
                    val runnerResult = RunnerResultCrossRef(runner.number, it.getId())
                    resultTables.add(result)
                    runnerResultCrossRefTables.add(runnerResult)
                }
            }
        }
        runnerDao.insertOrReplaceRunner(
            runnerTable,
            resultTables,
            teamNameTables,
            runnerResultCrossRefTables,
            distanceRunnerCrossRefTables
        )
    }

    private suspend fun updateRunner(runner: Runner) {
        deleteRunner(runner)
        insertRunnersTables(runner)
    }

    private suspend fun deleteRunner(runner: Runner) {
        runnerDao.delete(runner.number)
    }
}