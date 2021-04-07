package com.gmail.maystruks08.nfcruntracker.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.remote.Api
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
    private val api: Api
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result = coroutineScope {
        Timber.i("Sync runners worker STARTED")
        val resultOfTask = TaskResult.build {
            launch(Dispatchers.IO) {
                val runners = runnerDao.getNotUploadedRunners()
                runners.forEach {
//                    api.saveRunner(it.toFirestoreRunner())
                }
            }
        }
        when (resultOfTask) {
            is TaskResult.Value -> Result.success()
            is TaskResult.Error -> Result.retry()
            else -> Result.failure()
        }
    }
}