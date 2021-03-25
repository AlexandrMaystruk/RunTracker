package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteException
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerResultCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerWithResult
import com.gmail.maystruks08.data.local.entity.tables.ResultTable
import com.gmail.maystruks08.data.mappers.*
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.clearAndAddAll
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
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
    private val configPreferences: ConfigPreferences,
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
            .collect { runnerChangeList ->
                runnerChangeList.forEach {
                    val runner = it.entity.fromFirestoreRunner()
                    val canRewriteLocalCache = checkIsDataUploaded(runner.number)
                    if (canRewriteLocalCache) {
                        when (it.modifierType) {
                            ModifierType.ADD -> insertRunner(runner)
                            ModifierType.UPDATE -> updateRunner(runner)
                            ModifierType.REMOVE -> deleteRunner(runner)
                        }
                    }
                }
            }
    }

    override suspend fun getRunners(
        distanceId: String,
        query: String,
        onlyFinishers: Boolean
    ): List<Runner> {
        return runnerDao.getRunnerWithResultsQuery(distanceId, query)
            .mapNotNull { runnerWithResult ->
                when {
                    !onlyFinishers -> {
                        runnerWithResult.runnerTable.toRunner().apply {
                            this.checkpoints[distanceId] = runnerWithResult.getCheckpoints()
                        }
                    }
                    onlyFinishers -> {
                        val checkpoints = runnerWithResult.getCheckpoints(true)
                        return@mapNotNull if (checkpoints.isNotEmpty()) runnerWithResult.runnerTable.toRunner().apply {
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
        onlyFinishers: Boolean
    ): Flow<List<Runner>> {
        val actualDistanceId = if (distanceId == DEF_STRING_VALUE) distanceDAO.getFirstDistanceId() else distanceId
        return runnerDao.getRunnerWithResultsFlow(actualDistanceId).map { runnersWithResults ->
            runnersWithResults.mapNotNull {
                when {
                    !onlyFinishers -> {
                        it.runnerTable.toRunner().apply {
                            this.checkpoints[distanceId] = it.getCheckpoints()
                        }
                    }
                    onlyFinishers -> {
                        val checkpoints = it.getCheckpoints(true)
                        return@mapNotNull if (checkpoints.isNotEmpty()) it.runnerTable.toRunner().apply {
                            addCheckpoints(distanceId, checkpoints)
                        }
                        else null
                    }
                    else -> return@mapNotNull null
                }
            }
        }
    }

    override suspend fun getRunnerByCardId(cardId: String): Runner? {
        val runnerWithResultsTable = runnerDao.getRunnerWithResultsByCardId(cardId)
        return runnerWithResultsTable?.toRunner()
    }

    override suspend fun getRunnerByNumber(runnerNumber: Long): Runner? {
        return runnerDao.getRunnerWithResultsByNumber(runnerNumber)?.toRunner()
    }

    private fun RunnerWithResult.toRunner(): Runner {
        return runnerTable.toRunner().apply {
            this.raceIds.clearAndAddAll(runnerDao.getRunnerRaceIds(number))
            this.distanceIds.clearAndAddAll(runnerDao.getRunnerDistanceIds(number))
            this.checkpoints[actualDistanceId] = getCheckpoints()
        }
    }

    private fun RunnerWithResult.getCheckpoints(onlyFinishers: Boolean = false): MutableList<Checkpoint> {
        val distanceWithCheckpoints = distanceDAO.getDistance(runnerTable.actualDistanceId)
        val checkpointResult = results.distinct()
        if (onlyFinishers && checkpointResult.size != distanceWithCheckpoints.checkpoints.size) return mutableListOf()
        return distanceWithCheckpoints.checkpoints.map { checkpointTable ->
            val runnerResults = checkpointResult.firstOrNull { it.checkpointId == checkpointTable.checkpointId }
            val checkpoint = CheckpointImpl(
                checkpointTable.checkpointId,
                checkpointTable.distanceId,
                checkpointTable.name
            )
            if (runnerResults == null) checkpoint
            else CheckpointResultIml(
                checkpoint,
                runnerResults.time,
                runnerResults.hasPrevious
            )
        }.toMutableList()
    }

    override suspend fun getRunnerTeamMembers(
        currentRunnerNumber: Long,
        teamName: String
    ): List<Runner>? {
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


    private suspend fun checkIsDataUploaded(runnerNumber: Long): Boolean {
        return runnerDao.checkNeedToSync(runnerNumber) == null
    }

    private suspend fun insertRunner(runner: Runner) {
        val runnerTable = runner.toRunnerTable(false)
        val resultTables = mutableListOf<ResultTable>()
        val runnerResultCrossRefTables = mutableListOf<RunnerResultCrossRef>()
        val distanceRunnerCrossRefTables = runner.distanceIds.map { DistanceRunnerCrossRef(it, runner.number) }
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
            runnerResultCrossRefTables,
            distanceRunnerCrossRefTables
        )
        Timber.i("Insert runner ${runner.shortName}")
    }

    private suspend fun updateRunner(runner: Runner) {
        deleteRunner(runner)
        insertRunner(runner)
        Timber.i("Update runner ${runner.shortName}")
    }

    private suspend fun deleteRunner(runner: Runner) {
        val count = runnerDao.delete(runner.number)
        Timber.i("Removed runner ${runner.shortName} from DB count: $count")
    }
}