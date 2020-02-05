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
import com.gmail.maystruks08.data.remote.googledrive.GoogleDriveApi
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.*
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val driveApi: GoogleDriveApi,
    private val runnerDAO: RunnerDAO,
    private val runnersCache: RunnersCache,
    private val checkpointsCache: CheckpointsCache
) : RunnersRepository {

    override suspend fun bindGoogleDriveService(): ResultOfTask<Exception, String> {
        return try {
            withContext(Dispatchers.IO) {
                driveApi.getFile()
            }
            ResultOfTask.build { "" }
        } catch (e: NoClassDefFoundError) {
            ResultOfTask.build { throw Exception("https://accounts.google.com/o/oauth2/auth?access_type=online&client_id=7796872061-63b7kuf4ac15na6ur2lmp7brmt4ff8fg.apps.googleusercontent.com&redirect_uri=http://localhost:43240/Callback&response_type=code&scope=https://www.googleapis.com/auth/drive") }
        } catch (e: Exception) {
            ResultOfTask.build { throw e }
        }
    }

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

                                                    runnerDAO.insertRunner(
                                                        runner.toRunnerTable(),
                                                        runner.checkpoints.map {
                                                            it.toCheckpointTable(runner.id)
                                                        })
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
        try {
            withContext(Dispatchers.IO) {
                runnerDAO.updateRunner(
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
            Log.e(TAG, e.localizedMessage)
            withContext(Dispatchers.IO) {
                runnerDAO.markAsNeedToSync(runner.id, true)
            }
            return runner
        }
    }

    override suspend fun getRunnerById(cardId: String): Runner? = runnersCache.runnersList.find { it.id == cardId }

    override suspend fun getCurrentCheckpoint(type: RunnerType): Checkpoint =
        when (type) {
            RunnerType.NORMAL -> checkpointsCache.currentCheckpoint
            RunnerType.IRON -> checkpointsCache.currentIronPeopleCheckpoint
        }

    override suspend fun finishWork() {
        firestoreApi.unregisterUpdatesListener()
    }

    companion object {

        const val TAG = "RunnersRepository"
    }
}