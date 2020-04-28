package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toResultTable
import com.gmail.maystruks08.data.mappers.toRunner
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val runnersCache: RunnersCache,
    private val settingsCache: SettingsCache
) : RunnersRepository {

    override suspend fun getRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> {
        return try {
            ResultOfTask.build {
                when (type) {
                    RunnerType.NORMAL -> getNormalRunners()
                    RunnerType.IRON -> getIronRunners()
                }
            }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

    override suspend fun getNormalRunners(): List<Runner> {
        return if (runnersCache.normalRunnersList.isNotEmpty()) {
            runnersCache.normalRunnersList
        } else {
            val runners = runnerDao.getRunners(RunnerType.NORMAL.ordinal).map { it.toRunner() }
            runnersCache.normalRunnersList = runners.toMutableList()
            runnersCache.normalRunnersList
        }
    }

    override suspend fun getIronRunners(): List<Runner> {
        return if (runnersCache.ironRunnersList.isNotEmpty()) {
            runnersCache.ironRunnersList
        } else {
            val runners = runnerDao.getRunners(RunnerType.IRON.ordinal).map { it.toRunner() }
            runnersCache.ironRunnersList = runners.toMutableList()
            runnersCache.ironRunnersList
        }
    }

    override suspend fun getRunnerFinishers(): ResultOfTask<Exception, List<Runner>> {
        return try {
            if (runnersCache.normalRunnersList.isNotEmpty()) {
                val result = runnersCache.normalRunnersList.filter { it.totalResult != null }
                ResultOfTask.build { result }
            } else {
                val finishers = runnerDao.getRunnersFinishers().map { it.toRunner() }
                ResultOfTask.build { finishers }
            }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

    override suspend fun getIronRunnerFinishers(): ResultOfTask<Exception, List<Runner>> {
        return try {
            if (runnersCache.ironRunnersList.isNotEmpty()) {
                val result = runnersCache.ironRunnersList.filter { it.totalResult != null }
                ResultOfTask.build { result }
            } else {
                val finishers = runnerDao.getRunnersFinishers().map { it.toRunner() }
                ResultOfTask.build { finishers }
            }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) {
        try {
            firestoreApi.registerSnapshotListener { snapshot, firestoreException ->
                if (firestoreException != null) {
                    onResult.invoke(ResultOfTask.build { throw firestoreException })
                } else {
                    snapshot?.documentChanges?.forEach { documentChange ->
                        runBlocking {
                            launch {
                                val runner = documentChange.document.toObject(Runner::class.java)
                                val changeType = when (documentChange.type) {
                                    DocumentChange.Type.ADDED -> {
                                        insertRunner(runner)
                                        Change.ADD
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        updateRunner(runner)
                                        Change.UPDATE
                                    }
                                    DocumentChange.Type.REMOVED -> { deleteRunner(runner)
                                        Change.REMOVE
                                    }
                                }
                                if (type == runner.type) {
                                    onResult.invoke(ResultOfTask.build { RunnerChange(runner, changeType) })
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            onResult.invoke(ResultOfTask.build { throw SyncWithServerException() })
        }
    }

    override suspend fun updateRunnerData(runner: Runner): Runner? {
        return try {
            runnerDao.updateRunner(runner.toRunnerTable(), runner.checkpoints.map { it.toResultTable(runnerId = runner.id) })
            awaitTaskCompletable(firestoreApi.updateRunner(runner))

            val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.id == runner.id }
            if (index != -1) runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(runner)
            runner
        } catch (e: Exception) {
            e.printStackTrace()
            runnerDao.markAsNeedToSync(runner.id, true)
            runner
        }
    }

    override suspend fun getStartCheckpoints(): Pair<List<Checkpoint>, List<Checkpoint>> =
        settingsCache.checkpoints to settingsCache.checkpointsIronPeople

    override suspend fun getRunnerById(cardId: String): Runner? =
        runnersCache.normalRunnersList.find { it.id == cardId } ?: runnersCache.ironRunnersList.find { it.id == cardId }

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint = settingsCache.getCurrentCheckpoint(type)

    override suspend fun getCheckpointsCount(type: RunnerType): Int = settingsCache.getCheckpointList(type).size

    private suspend fun insertRunner(runner: Runner) {
        val runnerTable = runner.toRunnerTable()
        val resultTables = runner.checkpoints.map { it.toResultTable(runnerId = runner.id) }
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
            runnerDao.updateRunner(runner.toRunnerTable(), runner.checkpoints.map { it.toResultTable(runnerId = runner.id) })
            runnersCache.getRunnerList(runner.type).removeAt(index)
            runnersCache.getRunnerList(runner.type).add(index, runner)
        }
    }

    private suspend fun deleteRunner(runner: Runner) {
        runnerDao.delete(runner.number)
        runnersCache.getRunnerList(runner.type).removeAll { it.number == runner.number }
    }

    override suspend fun finishWork() {
        firestoreApi.unregisterUpdatesListener()
    }
}