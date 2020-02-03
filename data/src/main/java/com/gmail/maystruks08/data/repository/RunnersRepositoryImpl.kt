package com.gmail.maystruks08.data.repository

import android.util.Log
import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.CheckpointsCache
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.local.RunnerDAO
import com.gmail.maystruks08.data.mappers.toCheckpointTable
import com.gmail.maystruks08.data.mappers.toRunner
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDAO: RunnerDAO,
    private val runnersCache: RunnersCache,
    private val checkpointsCache: CheckpointsCache
) : RunnersRepository {

    override suspend fun getAllRunners(): ResultOfTask<Exception, List<Runner>> {
        return try {
            if (runnersCache.runnersList.isNotEmpty()) {
                ResultOfTask.build { runnersCache.runnersList }
            } else {
                val runners = runnerDAO.getRunners().map { it.toRunner() }
                runnersCache.runnersList = runners.toMutableList()
                ResultOfTask.build { runners }
            }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

    override suspend fun updateRunnersCache(onResult: (ResultOfTask<Exception, List<Runner>>) -> Unit) {
        try {
            withContext(Dispatchers.IO) {
                firestoreApi.registerSnapshotListener { snapshot, firestoreException ->
                    if (firestoreException != null) {
                        onResult.invoke(ResultOfTask.build { throw firestoreException })
                    } else {
                        snapshot?.documentChanges?.forEach { documentChange ->
                            when (documentChange.type) {
                                DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                                    documentChange.document.toObject(Runner::class.java)
                                        .let { runner ->
                                            runBlocking {
                                                launch {
                                                    runnerDAO.delete(runner.number)
                                                    runnersCache.runnersList.removeAll { it.number == runner.number }

                                                    runnerDAO.insertRunner(runner.toRunnerTable(), runner.checkpoints.map { it.toCheckpointTable(runner.id) })
                                                    runnersCache.runnersList.add(runner)
                                                }
                                            }
                                        }
                                }
                                DocumentChange.Type.REMOVED -> {
                                    documentChange.document.toObject(Runner::class.java)
                                        .let { runner ->
                                            runBlocking {
                                                launch {
                                                    runnerDAO.delete(runner.number)
                                                    runnersCache.runnersList.removeAll { it.number == runner.number }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                        onResult.invoke(ResultOfTask.build { runnersCache.runnersList })
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onResult.invoke(ResultOfTask.build { throw Throwable("Get and cache runners from firestore failure") })
        }
    }

    override suspend fun updateRunnerData(runner: Runner): Runner? {
        return try {
            awaitTaskCompletable(firestoreApi.updateRunner(runner))
            val index = runnersCache.runnersList.indexOfFirst { it.id == runner.id }
            if (index != -1) {
                runnersCache.runnersList.removeAt(index)
            }
            runnersCache.runnersList.add(runner)
            runner
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.localizedMessage)
            null
        }
    }

    override suspend fun getRunnerById(cardId: String): Runner? =
        runnersCache.runnersList.find { it.id == cardId }

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint {
        return when (type) {
            RunnerType.NORMAL -> checkpointsCache.currentCheckpoint
            RunnerType.IRON -> checkpointsCache.currentIronPeopleCheckpoint
        }
    }

    companion object {

        const val TAG = "RunnersRepository"
    }
}