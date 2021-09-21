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
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.DistanceType
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.FirebaseFirestoreException
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
    private val configPreferences: ConfigPreferences
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

    override suspend fun observeRunnerData(raceId: String): Flow<Unit> {
        return api
            .subscribeToRunnerCollectionChange(raceId)
            .map { runnerChangeList ->
                when (runnerChangeList.type) {
                    ModifierType.UPDATE -> runnerChangeList.runners.updateRunnersTable()
                    ModifierType.ADD -> runnerChangeList.runners.insert()
                    ModifierType.REMOVE -> runnerChangeList.runners.deleteRunnersTables()
                }
            }
    }

    override suspend fun getRunnersFlow(
        distance: Distance,
        onlyFinishers: Boolean,
        query: String?
    ): Flow<List<Runner>> {
        Timber.i("Get actual distance ${distance.name}")
        val actualRaceId = getRaceId()
        if (query.isNullOrEmpty()) {
            return flow {
                Timber.i("Start flow")
                val runnersWithResults = runnerDao.getRunnersWithResults(actualRaceId, distance.id)
                Timber.i("getRunnerWithResults ${runnersWithResults.count()}")
                val runners = runnersWithResults.mapNotNull {
                    val checkpoints = it.getCheckpoints(
                        onlyFinishers = onlyFinishers,
                        distanceCheckpoints = distance.checkpoints
                    )
                    when {
                        !onlyFinishers -> {
                            it.runnerTable.toRunner(checkpoints)
                        }
                        onlyFinishers -> {
                            if (checkpoints.isEmpty()) return@mapNotNull null
                            it.runnerTable.toRunner(checkpoints)
                        }
                        else -> return@mapNotNull null
                    }
                }
                Timber.i("emit(runners)")
                emit(runners)
            }
        }

        return flow {
            val runners = runnerDao
                .getRunnerWithResultsQuery(actualRaceId, distance.id, query)
                .mapNotNull {
                    val startTime = System.currentTimeMillis()
                    val checkpoints = it.getCheckpoints(
                        onlyFinishers = onlyFinishers,
                        distanceCheckpoints = distance.checkpoints
                    )
                    when {
                        !onlyFinishers -> {
                            val runner = it.runnerTable.toRunner(checkpoints)
                            Timber.d("MAPPING toRunner() time == ${System.currentTimeMillis() - startTime}")
                            runner
                        }
                        onlyFinishers -> {
                            if (checkpoints.isEmpty()) return@mapNotNull null
                            it.runnerTable.toRunner(checkpoints)
                        }
                        else -> return@mapNotNull null
                    }
                }
            emit(runners)
        }
    }

    override suspend fun getTeamRunnersFlow(
        distance: Distance,
        onlyFinishers: Boolean
    ): Flow<List<Team>> {
        val actualRaceId = getRaceId()
        return flow {
            val runnersWithResults = runnerDao.getTeamRunnersWithResults(actualRaceId, distance.id)
            val teams = runnersWithResults
                .groupBy { it.team?.name }
                .mapNotNull { entry ->
                    val teamName = entry.key ?: return@mapNotNull null

                    if (distance.type != DistanceType.REPLAY) {
                        return@mapNotNull Team(
                            teamName = teamName,
                            distanceType = distance.type,
                            runners = entry.value.map {
                                val checkpoints = it.getCheckpoints(distance.checkpoints)
                                it.runnerTable.toRunner(checkpoints)
                            },
                        )
                    }

                    val runners = entry.value.sortedBy { it.runnerTable.runnerNumber }
                        .mapIndexed runnerMapNotNull@{ index, runnerWithResult ->
                            with(runnerWithResult) {
                                val checkpoints = getCheckpoints(distance.checkpoints)
                                val replayCheckpointIndex = checkpoints.size / 2
                                if (index % 2 == 0) {
                                    return@runnerMapNotNull runnerTable.toRunner(
                                        checkpoints.subList(0, replayCheckpointIndex)
                                    )
                                }
                                return@runnerMapNotNull runnerTable.toRunner(
                                    checkpoints.subList(replayCheckpointIndex - 1, checkpoints.size)
                                )
                            }
                        }
                    return@mapNotNull Team(teamName, runners, distance.type)
                }
            emit(teams)
        }
    }

    override suspend fun getRunnerByCardId(cardId: String): Runner? {
        val runnerWithResultsTable = runnerDao.getRunnerWithResultsByCardId(cardId) ?: return null
        val distanceCheckpoints = distanceDAO
            .getDistanceCheckpoints(runnerWithResultsTable.runnerTable.actualDistanceId)
            .map { it.toCheckpoint() }
        val checkpoints = runnerWithResultsTable.getCheckpoints(distanceCheckpoints)
        return runnerWithResultsTable.runnerTable.toRunner(checkpoints)
    }

    override suspend fun getRunnerByNumber(runnerNumber: String): Runner? {
        val runnerWithResultsTable = runnerDao.getRunnerWithResultsByNumber(runnerNumber) ?: return null
        val distanceCheckpoints = distanceDAO
            .getDistanceCheckpoints(runnerWithResultsTable.runnerTable.actualDistanceId)
            .map { it.toCheckpoint() }
        val checkpoints = runnerWithResultsTable.getCheckpoints(distanceCheckpoints)
        return runnerWithResultsTable.runnerTable.toRunner(checkpoints)
    }

    override suspend fun getTeam(teamName: String): Team? {
        val runnersTables = runnerDao.getTeamRunnersWithResultsByName(teamName)
        if (runnersTables.isNullOrEmpty()) return null
        val actualDistanceId = runnersTables.first().runnerTable.actualDistanceId
        val typeString = distanceDAO.getDistanceTypeById(actualDistanceId)
        val distanceType = DistanceType.valueOf(typeString)
        val distanceCheckpoints = distanceDAO.getDistanceCheckpoints(actualDistanceId).map { it.toCheckpoint() }
        if (distanceType != DistanceType.REPLAY) {
            return Team(
                teamName = teamName,
                distanceType = distanceType,
                runners = runnersTables.map {
                    val checkpoints = it.getCheckpoints(distanceCheckpoints)
                    it.runnerTable.toRunner(checkpoints)
                },
            )
        }
        val runners = runnersTables.sortedBy { it.runnerTable.runnerNumber }
            .mapIndexed runnerMapNotNull@{ index, runnerWithResult ->
                with(runnerWithResult) {
                    val checkpoints = getCheckpoints(distanceCheckpoints)
                    val replayCheckpointIndex = checkpoints.size / 2
                    if (index % 2 == 0) {
                        return@runnerMapNotNull runnerTable.toRunner(
                            checkpoints.subList(0, replayCheckpointIndex)
                        )
                    }
                    return@runnerMapNotNull runnerTable.toRunner(
                        checkpoints.subList(replayCheckpointIndex - 1, checkpoints.size)
                    )
                }
            }
        return Team(teamName, runners, distanceType)
    }

//    private fun RunnerWithResult.toRunner(): Runner {
//        val startTime = System.currentTimeMillis()
//        return runnerTable
//            .toRunner(
//                getCheckpoints()
//            )
//            .also {
//                Timber.d("MAPPING toRunner() time == ${System.currentTimeMillis() - startTime}")
//            }
//    }

    private fun RunnerWithResult.getCheckpoints(
        distanceCheckpoints: List<Checkpoint>,
        onlyFinishers: Boolean = false
    ): MutableList<Checkpoint> {
        val checkpointResult = results.distinct()
        if (onlyFinishers && checkpointResult.size != distanceCheckpoints.size) return mutableListOf()
        val result = mutableListOf<Checkpoint>()
        distanceCheckpoints.forEach { distanceCheckpoint ->
            val runnerResults =
                checkpointResult.firstOrNull { it.checkpointId == distanceCheckpoint.getId() }
                    ?: kotlin.run {
                        result.add(distanceCheckpoint)
                        return@forEach
                    }
            result.add(
                CheckpointResultIml(
                    distanceCheckpoint,
                    runnerResults.time,
                    runnerResults.hasPrevious
                )
            )
        }
        result.sortBy { it.getPosition() }
        return result
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
            val runnerTable = runner.toRunnerTable(false).also { runners.add(it) }
            runner.currentCheckpoints
                .filterIsInstance<CheckpointResultIml>()
                .map { it.toResultTable(runnerTable.runnerNumber) }
                .also { resultTables.addAll(it) }

            resultTables.forEach {
                RunnerResultCrossRef(
                    runner.number,
                    it.checkpointId
                ).also { runnerResultCrossRefTables.add(it) }
            }

            DistanceRunnerCrossRef(
                runner.actualDistanceId,
                runner.number
            ).also { distanceRunnerCrossRefTables.add(it) }

            runner.currentTeamName?.let {
                TeamNameTable(
                    distanceId = runner.actualDistanceId,
                    runnerId = runner.number,
                    name = it
                )
            }?.also { teamNameTables.add(it) }
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
        val runnerTable = runner.toRunnerTable(false)
        val resultTables = runner.currentCheckpoints
            .filterIsInstance<CheckpointResultIml>()
            .map { it.toResultTable(runnerTable.runnerNumber) }
        val runnerResultCrossRefTables =
            resultTables.map { RunnerResultCrossRef(runner.number, it.checkpointId) }
        val distanceRunnerCrossRefTables =
            mutableListOf(DistanceRunnerCrossRef(runner.actualDistanceId, runner.number))
        val teamNameTables = mutableListOf<TeamNameTable>().apply {
            runner.currentTeamName?.let {
                TeamNameTable(
                    distanceId = runner.actualDistanceId,
                    runnerId = runner.number,
                    name = it
                )
            }?.also { add(it) }
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