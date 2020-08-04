package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersWorkHelper
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SyncModule {

    @Binds
    @Singleton
    abstract fun provideSyncRunnersDataScheduler(syncRunnersWorkHelper: SyncRunnersWorkHelper): SyncRunnersDataScheduler

}