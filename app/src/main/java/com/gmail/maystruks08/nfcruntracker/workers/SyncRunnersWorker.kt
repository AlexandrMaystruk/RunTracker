package com.gmail.maystruks08.nfcruntracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toCheckpoint
import com.gmail.maystruks08.data.mappers.toRunners
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.CheckpointType
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.nfcruntracker.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SyncRunnersWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @Inject
    lateinit var runnerDao: RunnerDao

    @Inject
    lateinit var checkpointDAO: CheckpointDAO

    @Inject
    lateinit var firestoreApi: FirestoreApi

    override suspend fun doWork(): Result = coroutineScope {
        Timber.i("Sync runners worker STARTED")
        App.hostComponent?.inject(this@SyncRunnersWorker)
        val resultOfTask = ResultOfTask.build {
            launch(Dispatchers.IO) {
                val checkpoints = async { checkpointDAO.getCheckpointsByType(CheckpointType.NORMAL.ordinal).map { it.toCheckpoint() } }
                val ironCheckpoints = async { checkpointDAO.getCheckpointsByType(CheckpointType.IRON.ordinal).map { it.toCheckpoint() } }
                val runnerWithIronRunner = runnerDao.getNotUploadedRunnersWithResults().partition { it.runnerTable.type == RunnerType.NORMAL.ordinal }
                Timber.i("Not uploaded runner count ${runnerWithIronRunner.first.size + runnerWithIronRunner.second.size}")
                runnerWithIronRunner.first.toRunners(checkpoints.await()).forEach {
                    firestoreApi.updateRunner(it)
                    runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = false)
                }
                runnerWithIronRunner.second.toRunners( ironCheckpoints.await()).forEach {
                    firestoreApi.updateRunner(it)
                    runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = false)
                }
            }
        }
        when (resultOfTask) {
            is ResultOfTask.Value -> Result.success()
            is ResultOfTask.Error -> Result.retry()
            else -> Result.failure()
        }
    }
}