package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.*
import com.gmail.maystruks08.domain.interactors.*
import com.gmail.maystruks08.domain.repository.*
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

@FlowPreview
@ExperimentalCoroutinesApi
@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    @ActivityScoped
    abstract fun bindLogoutUseCase(impl: LogOutUseCaseImpl): LogOutUseCase


    @Binds
    @ActivityScoped
    abstract fun bindRunnersInteractor(impl: RunnersInteractorImpl): RunnersInteractor


    @Binds
    @ActivityScoped
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

    @Binds
    @ActivityScoped
    abstract fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener


    @Binds
    @ActivityScoped
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository


    @Binds
    @ActivityScoped
    abstract fun bindCheckpointInteractor(impl: CheckpointInteractorImpl): CheckpointInteractor

    @Binds
    @ActivityScoped
    abstract fun bindCheckpointsRepository(impl: CheckpointsRepositoryImpl): CheckpointsRepository


    @Binds
    @ActivityScoped
    abstract fun bindRaceInteractor(impl: RaceInteractorImpl): RaceInteractor

    @Binds
    @ActivityScoped
    abstract fun bindRaceRepository(impl: RaceRepositoryImpl): RaceRepository

    @Binds
    @ActivityScoped
    abstract fun bindCreateRaceUseCase(impl: CreateRaceUseCaseImpl): CreateRaceUseCase


    @Binds
    @ActivityScoped
    abstract fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository

    @Binds
    @ActivityScoped
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerInteractor


    @Binds
    @ActivityScoped
    abstract fun bindDistanceInteractor(impl: DistanceInteractorImpl): DistanceInteractor

    @Binds
    @ActivityScoped
    abstract fun bindDistanceRepository(impl: DistanceRepositoryImpl): DistanceRepository


    @Binds
    @ActivityScoped
    abstract fun bindDistanceStatisticUseCase(impl: CalculateDistanceStatisticUseCaseImpl): CalculateDistanceStatisticUseCase

    @Binds
    @ActivityScoped
    abstract fun bindDistanceStatisticRepository(impl: DistanceStatisticRepositoryImpl): DistanceStatisticRepository


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