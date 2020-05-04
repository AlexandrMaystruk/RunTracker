package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.LogHelperImpl
import com.gmail.maystruks08.data.repository.SettingsRepositoryImpl
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun log(logHelper: LogHelperImpl): LogHelper

}