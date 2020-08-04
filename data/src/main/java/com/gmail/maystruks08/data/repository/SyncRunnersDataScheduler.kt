package com.gmail.maystruks08.data.repository

import java.util.concurrent.TimeUnit

interface SyncRunnersDataScheduler {

    fun startSyncData(period: Long, timeUnit: TimeUnit)

    fun stopAllWork()

}