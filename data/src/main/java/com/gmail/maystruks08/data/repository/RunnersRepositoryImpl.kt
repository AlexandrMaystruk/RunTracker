package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.CheckpointsCache
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.local.RunnerDao
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.mappers.toRunner
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val runnersCache: RunnersCache,
    private val checkpointsCache: CheckpointsCache
) : RunnersRepository {

    override suspend fun getAllRunners(type: RunnerType): ResultOfTask<Exception, List<Runner>> {
        return try {
            if (runnersCache.runnersList.isNotEmpty()) {
                val result = runnersCache.runnersList.filter { it.type == type }
                ResultOfTask.build { result }
            } else {
                val runners = runnerDao.getRunners().map { it.toRunner() }
                runnersCache.runnersList = runners.toMutableList()
                val result = runnersCache.runnersList.filter { it.type == type }
                ResultOfTask.build { result }
            }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

    override suspend fun updateRunnersCache(type: RunnerType, onResult: (ResultOfTask<Exception, RunnerChange>) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
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
                                        DocumentChange.Type.REMOVED -> {
                                            deleteRunner(runner)
                                            Change.REMOVE
                                        }
                                    }
                                    if (type == runner.type) {
                                        onResult.invoke(ResultOfTask.build {
                                            RunnerChange(
                                                runner,
                                                changeType
                                            )
                                        })
                                    }
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
        try {
            withContext(Dispatchers.IO) {
                runnerDao.updateRunner(
                    runner.toRunnerTable(),
                    runner.checkpoints.map { it.toCheckpointTable(runnerId = runner.id) })
            }
            awaitTaskCompletable(firestoreApi.updateRunner(runner))
            val index = runnersCache.runnersList.indexOfFirst { it.id == runner.id }
            if (index != -1) {
                runnersCache.runnersList.removeAt(index)
            }
            runnersCache.runnersList.add(runner)
            return runner
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                runnerDao.markAsNeedToSync(runner.id, true)
            }
            return runner
        }
    }

    override suspend fun getRunnerById(cardId: String): Runner? =
        runnersCache.runnersList.find { it.id == cardId }

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint =
        when (type) {
            RunnerType.NORMAL -> checkpointsCache.currentCheckpoint
            RunnerType.IRON -> checkpointsCache.currentIronPeopleCheckpoint
        }

    override suspend fun finishWork() {
        firestoreApi.unregisterUpdatesListener()
    }

    private suspend fun insertRunner(runner: Runner) {
        val runnerTable = runner.toRunnerTable()
        val checkpointsTables = runner.checkpoints.map { it.toCheckpointTable(runner.id) }
        val index = runnersCache.runnersList.indexOfFirst { it.number == runner.number }
        if (index != -1) {
            runnerDao.updateRunner(runnerTable, checkpointsTables)
            runnersCache.runnersList.removeAt(index)
            runnersCache.runnersList.add(index, runner)
        } else {
            runnerDao.insertRunner(runnerTable, checkpointsTables)
            runnersCache.runnersList.add(runner)
        }
    }

    private suspend fun updateRunner(runner: Runner) {
        val index = runnersCache.runnersList.indexOfFirst { it.number == runner.number }
        if (index != -1) {
            runnerDao.updateRunner(
                runner.toRunnerTable(),
                runner.checkpoints.map { it.toCheckpointTable(runner.id) })
            runnersCache.runnersList.removeAt(index)
            runnersCache.runnersList.add(index, runner)
        }
    }

    private suspend fun deleteRunner(runner: Runner) {
        runnerDao.delete(runner.number)
        runnersCache.runnersList.removeAll { it.number == runner.number }
    }
}