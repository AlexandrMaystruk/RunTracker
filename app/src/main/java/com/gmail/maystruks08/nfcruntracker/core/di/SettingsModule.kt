package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.LogHelperImpl
import com.gmail.maystruks08.data.repository.SettingsRepositoryImpl
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.di.host.HostScope
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {

    @Binds
    @HostScope
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @HostScope
    abstract fun log(logHelper: LogHelperImpl): LogHelper

}