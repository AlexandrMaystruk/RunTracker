package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toCheckpointsResult
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.mappers.toRunners
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.toDateTimeShortFormat
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val runnersCache: RunnersCache,
    private val settingsCache: SettingsCache
) : RunnersRepository {

    override suspend fun getRunners(type: RunnerType, onlyFinishers: Boolean): List<Runner> =
        when (type) {
            RunnerType.NORMAL -> getNormalRunners(onlyFinishers)
            RunnerType.IRON -> getIronRunners(onlyFinishers)
        }

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) {
        try {
            firestoreApi.subscribeToRunnerDataRealtimeUpdates {
                it.forEach { change ->
                    runBlocking {
                        val resultOfTask = ResultOfTask.build {
                            when (change.changeType) {
                                Change.ADD -> insertRunner(change.runner)
                                Change.UPDATE -> updateRunner(change.runner)
                                Change.REMOVE -> deleteRunner(change.runner)
                            }
                            change
                        }
                        if (type == change.runner.type) onResult.invoke(resultOfTask)
                    }
                }
            }
        } catch (e: Exception) {
            onResult.invoke(ResultOfTask.build { throw SyncWithServerException() })
        }
    }

    override suspend fun updateRunnerData(runner: Runner): Runner {
        try {
            Timber.log(Log.INFO, "Saving runner data: ${runner.id} checkpoints:${runner.checkpoints.map { "${it.name} ${if (it is CheckpointResult) it.date.toDateTimeShortFormat() else ""}" }}")
            runnerDao.updateRunner(runner.toRunnerTable(), runner.checkpoints.toCheckpointsResult(runner.id))
            val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.id == runner.id }
            if (index != -1) runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(runner)
        } catch (e: SQLiteException) {
            Timber.e(e, "Saving runner ${runner.id} data to room error")
            e.printStackTrace()
            throw SaveRunnerDataException(runner.fullName)
        }
        try {
            awaitTaskCompletable(firestoreApi.updateRunner(runner))
        } catch (e: FirebaseFirestoreException) {
            Timber.e(e, "Saving runner ${runner.id} data to firestore error")
            e.printStackTrace()
            runnerDao.markAsNeedToSync(runner.id, true)
            throw SyncWithServerException()
        }
        return runner
    }

    override suspend fun getCheckpoints(type: RunnerType): List<Checkpoint> =
        settingsCache.getCheckpointList(type)

    override suspend fun getRunnerById(cardId: String): Runner? = runnersCache.findRunner(cardId)

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint =
        settingsCache.getCurrentCheckpoint(type)

    private fun getNormalRunners(onlyFinishers: Boolean): List<Runner> {
        if (runnersCache.normalRunnersList.isEmpty()) {
            val runners = runnerDao.getRunnersWithResults(RunnerType.NORMAL.ordinal)
            if (settingsCache.getCheckpointList(RunnerType.NORMAL).isEmpty()) {
                settingsCache.checkpoints = runnerDao.getCheckpoints(CheckpointType.NORMAL.ordinal).map { it.toCheckpoint() }
            }
            val checkpoints = settingsCache.getCheckpointList(RunnerType.NORMAL)
            runnersCache.normalRunnersList = runners.toRunners(checkpoints).toMutableList()
        }
        return if (onlyFinishers) runnersCache.normalRunnersList.filter { it.totalResult != null } else runnersCache.normalRunnersList

    }

    private fun getIronRunners(onlyFinishers: Boolean): List<Runner> {
        if (runnersCache.ironRunnersList.isEmpty()) {
            val runners = runnerDao.getRunnersWithResults(RunnerType.IRON.ordinal)
            val checkpoints = settingsCache.getCheckpointList(RunnerType.IRON)
            if (checkpoints.isEmpty()) {
                settingsCache.checkpointsIronPeople = runnerDao.getCheckpoints(CheckpointType.IRON.ordinal).map { it.toCheckpoint() }
            }
            runnersCache.ironRunnersList = runners.toRunners(settingsCache.checkpointsIronPeople).toMutableList()
        }
        return if (onlyFinishers) runnersCache.ironRunnersList.filter { it.totalResult != null } else runnersCache.ironRunnersList
    }

    private suspend fun insertRunner(runner: Runner) {
        withContext(Dispatchers.IO) {
            val runnerTable = runner.toRunnerTable()
            val resultTables = runner.checkpoints.toCheckpointsResult(runner.id)
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
    }

    private suspend fun updateRunner(runner: Runner) {
        withContext(Dispatchers.IO) {
            val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
            if (index != -1) {
                runnerDao.updateRunner(runner.toRunnerTable(), runner.checkpoints.toCheckpointsResult(runner.id))
                runnersCache.getRunnerList(runner.type).removeAt(index)
                runnersCache.getRunnerList(runner.type).add(index, runner)
            }
        }
    }

    private suspend fun deleteRunner(runner: Runner) {
        withContext(Dispatchers.IO) {
            val count = runnerDao.delete(runner.id)
            val isRemoved = runnersCache.getRunnerList(runner.type).removeAll { it.id == runner.id }
            Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
        }
    }

    override suspend fun finishWork() {
        firestoreApi.unregisterUpdatesListener()
    }
}