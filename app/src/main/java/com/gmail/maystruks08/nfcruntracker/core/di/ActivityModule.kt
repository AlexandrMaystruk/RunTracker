package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.RunnersRepositoryImpl
import com.gmail.maystruks08.data.repository.SettingsRepositoryImpl
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.CheckpointInteractorImpl
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractorImpl
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCase
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    @ActivityScoped
    abstract fun provideLogoutUseCase(logOutUseCase: LogOutUseCaseImpl): LogOutUseCase

    @Binds
    @ActivityScoped
    abstract fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener

    @Binds
    @ActivityScoped
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

    @Binds
    @ActivityScoped
    abstract fun bindRunnersInteractor(impl: RunnersInteractorImpl): RunnersInteractor

    @Binds
    @ActivityScoped
    abstract fun bindCheckpointInteractor(impl: CheckpointInteractorImpl): CheckpointInteractor

    @Binds
    @ActivityScoped
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

}

@Module
@InstallIn(ActivityComponent::class)
object NavigationModule {

    @Provides
    @ActivityScoped
    fun cicerone(): Cicerone<Router> = Cicerone.create()

    @Provides
    @ActivityScoped
    fun router(cicerone: Cicerone<Router>): Router = cicerone.router

    @Provides
    @ActivityScoped
    fun navigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder = cicerone.navigatorHolder

}