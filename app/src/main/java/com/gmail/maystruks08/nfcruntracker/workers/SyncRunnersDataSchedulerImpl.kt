package com.gmail.maystruks08.nfcruntracker.workers

import android.content.Context
import androidx.work.*
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SyncRunnersDataSchedulerImpl @Inject constructor(@ApplicationContext context: Context) : SyncRunnersDataScheduler {

    private val mWorkManager: WorkManager = WorkManager.getInstance(context)

    override fun startSyncData(period: Long, timeUnit: TimeUnit) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRunnersDataTaskWorkRequest = PeriodicWorkRequest
            .Builder(SyncRunnersWorker::class.java, period, timeUnit)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, BACKOFF_DELAY_SECONDS, TimeUnit.SECONDS)
            .addTag(UPLOAD_RUNNER_DATA_WORK_TAG)
            .build()

        mWorkManager.enqueueUniquePeriodicWork(
            UPLOAD_RUNNER_DATA_PERIODIC,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRunnersDataTaskWorkRequest
        )
    }

    override fun stopAllWork() {
        mWorkManager.cancelAllWorkByTag(UPLOAD_RUNNER_DATA_WORK_TAG)
    }


    companion object {

        const val UPLOAD_RUNNER_DATA_WORK_TAG = "UPLOAD_RUNNER_DATA_WORK_TAG"

        private const val UPLOAD_RUNNER_DATA_PERIODIC = "UPLOAD_RUNNER_DATA_PERIODIC"

        private const val BACKOFF_DELAY_SECONDS = 60L

    }
}