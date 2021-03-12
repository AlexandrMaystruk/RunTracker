package com.gmail.maystruks08.nfcruntracker.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.TaskResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressWarnings("unchecked")
class SyncRunnersWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val runnerDao: RunnerDao,
    private val checkpointDAO: CheckpointDAO,
    private val firestoreApi: FirestoreApi
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result = coroutineScope {
        Timber.i("Sync runners worker STARTED")
        val resultOfTask = TaskResult.build {
            launch(Dispatchers.IO) {
//                val checkpoints = async { checkpointDAO.getCheckpointsByType(CheckpointType.NORMAL.ordinal).map { it.toCheckpoint() } }
//                val ironCheckpoints = async { checkpointDAO.getCheckpointsByType(CheckpointType.IRON.ordinal).map { it.toCheckpoint() } }
//                val runnerWithIronRunner = runnerDao.getNotUploadedRunnersWithResults().partition { it.runnerTable.type == RunnerType.NORMAL.ordinal }
//                Timber.i("Not uploaded runner count ${runnerWithIronRunner.first.size + runnerWithIronRunner.second.size}")
//                runnerWithIronRunner.first.toRunners(checkpoints.await()).forEach {
//                    firestoreApi.updateRunner(it)
//                    runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = false)
//                }
//                runnerWithIronRunner.second.toRunners(ironCheckpoints.await()).forEach {
//                    firestoreApi.updateRunner(it)
//                    runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = false)
//                }
            }
        }
        when (resultOfTask) {
            is TaskResult.Value -> Result.success()
            is TaskResult.Error -> Result.retry()
            else -> Result.failure()
        }
    }
}