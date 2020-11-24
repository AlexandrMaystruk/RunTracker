package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.*
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointType
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeShortFormat
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import timber.log.Timber
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val runnersCache: RunnersCache,
    private val settingsCache: SettingsCache,
    private val networkUtil: NetworkUtil
) : RunnersRepository, RunnerDataChangeListener {

    override suspend fun getRunners(type: RunnerType, onlyFinishers: Boolean, initSize: Int?): List<Runner> =
        when (type) {
            RunnerType.NORMAL -> getNormalRunners(onlyFinishers, initSize)
            RunnerType.IRON -> getIronRunners(onlyFinishers, initSize)
        }

    override suspend fun updateRunnerData(runner: Runner): Runner {
        try {
            Timber.log(Log.INFO, "Saving runner data: ${runner.number} checkpoints:${runner.checkpoints.map { "${it.name} ${if (it is CheckpointResult) it.date.toDateTimeShortFormat() else ""}" }}")
            runnerDao.updateRunner(runner.toRunnerTable(), runner.checkpoints.toCheckpointsResult(runner.number))
            val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
            if (index != -1) runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(runner)
        } catch (e: SQLiteException) {
            Timber.e(e, "Saving runner ${runner.number} data to room error")
            e.printStackTrace()
            throw SaveRunnerDataException(runner.fullName)
        }
        if (networkUtil.isOnline()) {
            try {
                runnerDao.markAsNeedToSync(runner.number, false)
                awaitTaskCompletable(firestoreApi.updateRunner(runner))
            } catch (e: FirebaseFirestoreException) {
                Timber.e(e, "Saving runner ${runner.number} data to firestore error")
                runnerDao.markAsNeedToSync(runner.number, true)
                throw SyncWithServerException()
            }
        }
        return runner
    }

    override suspend fun getCheckpoints(type: RunnerType): List<Checkpoint> = settingsCache.getCheckpointList(type)

    override suspend fun getRunnerByCardId(cardId: String): Runner? = runnersCache.findRunnerByCardId(cardId)

    override suspend fun getRunnerByNumber(runnerNumber: Int): Runner? = runnersCache.findRunnerByNumber(runnerNumber)

    override suspend fun getRunnerTeamMembers(currentRunnerNumber: Int, teamName: String): List<Runner>? = runnersCache.findRunnerTeamMembers(currentRunnerNumber, teamName)

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint = settingsCache.getCurrentCheckpoint(type)

    private fun getNormalRunners(onlyFinishers: Boolean, initSize: Int? = null): List<Runner> {
        if (runnersCache.normalRunnersList.isEmpty()) {
            val runners = runnerDao.getRunnersWithResults(RunnerType.NORMAL.ordinal)
            if (settingsCache.getCheckpointList(RunnerType.NORMAL).isEmpty()) {
                settingsCache.checkpoints = runnerDao.getCheckpoints(CheckpointType.NORMAL.ordinal).toCheckpoints()
            }
            runners.forEach {
                val runner = it.toRunner(settingsCache.checkpoints)
                runnersCache.normalRunnersList.add(runner)
            }
        }
        val result = runnersCache.normalRunnersList.run { if (onlyFinishers) filter { it.totalResult != null && !it.isOffTrack } else this }
        return result.let {
            if (initSize != null) {
                try {
                    it.take(initSize)
                } catch (e: IllegalArgumentException) {
                    it.toMutableList()
                }
            } else it.toMutableList()
        }
    }

    private fun getIronRunners(onlyFinishers: Boolean, initSize: Int? = null): List<Runner> {
        if (runnersCache.ironRunnersList.isEmpty()) {
            val runners = runnerDao.getRunnersWithResults(RunnerType.IRON.ordinal)
            if (settingsCache.getCheckpointList(RunnerType.IRON).isEmpty()) {
                settingsCache.checkpointsIronPeople = runnerDao.getCheckpoints(CheckpointType.IRON.ordinal).toCheckpoints()
            }
            runners.forEach {
                val runner = it.toRunner(settingsCache.checkpointsIronPeople)
                runnersCache.ironRunnersList.add(runner)
            }
        }
        val result = runnersCache.ironRunnersList.run { if (onlyFinishers) filter { it.totalResult != null && !it.isOffTrack } else this }
        return result.let {
            if (initSize != null) {
                try {
                    it.take(initSize)
                } catch (e: IllegalArgumentException) {
                    it.toMutableList()
                }
            } else it.toMutableList()
        }
    }

    private suspend fun checkIsDataUploaded(runnerNumber: Int): Boolean {
        return runnerDao.checkNeedToSync(runnerNumber) == null
    }

    private suspend fun insertRunner(runner: Runner) {
        val runnerTable = runner.toRunnerTable(false)
        val resultTables = runner.checkpoints.toCheckpointsResult(runner.number)
        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
        if (index != -1) {
            runnerDao.updateRunner(runnerTable, resultTables)
            runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(index, runner)
        } else {
            runnerDao.insertOrReplaceRunner(runnerTable, resultTables)
            runnersCache.getRunnerList(runner.type).add(runner)
        }
    }

    private suspend fun updateRunner(runner: Runner) {
        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
        if (index != -1) {
            runnerDao.updateRunner(runner.toRunnerTable(false), runner.checkpoints.toCheckpointsResult(runner.number))
            runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(index, runner)
        }
    }

    private suspend fun deleteRunner(runner: Runner) {
        val count = runnerDao.delete(runner.number)
        val isRemoved = runnersCache.getRunnerList(runner.type).removeAll { it.number == runner.number }
        Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override suspend fun observeRunnerData(): Flow<RunnerChange> {
        return firestoreApi.subscribeToRunnerDataRealtimeUpdates().flatMapConcat { runnerChangeList ->
            runnerChangeList.forEach {
                val canRewriteLocalCache = checkIsDataUploaded(it.runner.number)
                if (canRewriteLocalCache) {
                    when (it.changeType) {
                        Change.ADD -> insertRunner(it.runner)
                        Change.UPDATE -> updateRunner(it.runner)
                        Change.REMOVE -> deleteRunner(it.runner)
                    }
                }
            }
           return@flatMapConcat channelFlow { runnerChangeList.forEach { offer(it) } }
        }
    }
}