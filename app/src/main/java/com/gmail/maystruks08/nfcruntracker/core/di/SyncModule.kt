package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.nfcruntracker.core.di.host.HostScope
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersWorkHelper
import dagger.Binds
import dagger.Module

@Module
abstract class SyncModule {

    @Binds
    @HostScope
    abstract fun provideSyncRunnersDataScheduler(syncRunnersWorkHelper: SyncRunnersWorkHelper): SyncRunnersDataScheduler

}