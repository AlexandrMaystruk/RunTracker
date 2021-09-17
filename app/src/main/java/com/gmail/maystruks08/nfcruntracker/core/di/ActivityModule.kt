package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.*
import com.gmail.maystruks08.domain.interactors.RaceInteractor
import com.gmail.maystruks08.domain.interactors.RaceInteractorImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractorImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerUseCase
import com.gmail.maystruks08.domain.interactors.use_cases.*
import com.gmail.maystruks08.domain.interactors.use_cases.runner.*
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
    abstract fun bindGetAccountAccessLevelUseCase(impl: GetAccountAccessLevelUseCaseImpl): GetAccountAccessLevelUseCase

    @Binds
    @ActivityScoped
    abstract fun bindExportFromXlsToRemoteUseCase(impl: ExportFromXlsToRemoteUseCaseImpl): ExportFromXlsToRemoteUseCase

    @Binds
    @ActivityScoped
    abstract fun bindProvideCheckpointsUseCase(impl: ProvideCheckpointsUseCaseImpl): ProvideCheckpointsUseCase

    @Binds
    @ActivityScoped
    abstract fun bindCheckpointsRepository(impl: CheckpointsRepositoryImpl): CheckpointsRepository


    @Binds
    @ActivityScoped
    abstract fun bindRaceInteractor(impl: RaceInteractorImpl): RaceInteractor

    @Binds
    @ActivityScoped
    abstract fun bindCreateRaceUseCase(impl: CreateRaceUseCaseImpl): CreateRaceUseCase

    @Binds
    @ActivityScoped
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerUseCase


    @Binds
    @ActivityScoped
    abstract fun bindDistanceStatisticUseCase(impl: CalculateDistanceStatisticUseCaseImpl): CalculateDistanceStatisticUseCase

    @Binds
    @ActivityScoped
    abstract fun bindSaveCheckpointsUseCase(impl: SaveCheckpointsUseCaseImpl): SaveCheckpointsUseCase

    @Binds
    @ActivityScoped
    abstract fun bindUpdateDistanceNameUseCase(impl: UpdateDistanceNameUseCaseImpl): UpdateDistanceNameUseCase


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

@Module
@InstallIn(ActivityComponent::class)
interface RunnerUseCaseModule {

    @Binds
    @ActivityScoped
    fun bindProvideRunnersUseCase(impl: ProvideRunnersUseCaseImpl): ProvideRunnersUseCase

    @Binds
    @ActivityScoped
    fun bindProvideFinishersUseCase(impl: ProvideFinishersUseCaseImpl): ProvideFinishersUseCase

    @Binds
    @ActivityScoped
    fun bindProvideDistanceListUseCase(impl: ProvideDistanceListUseCaseImpl): ProvideDistanceListUseCase

    @Binds
    @ActivityScoped
    fun bindProvideCurrentRaceIdUseCase(impl: ProvideCurrentRaceIdUseCaseImpl): ProvideCurrentRaceIdUseCase

    @Binds
    @ActivityScoped
    fun bindProvideRunnerUseCase(impl: ProvideRunnerUseCaseImpl): ProvideRunnerUseCase

    @Binds
    @ActivityScoped
    fun bindManageRunnerCheckpointInteractor(impl: ManageRunnerCheckpointInteractorImpl): ManageRunnerCheckpointInteractor


    @Binds
    @ActivityScoped
    fun bindGetCurrentSelectedCheckpointUseCase(impl: GetCurrentSelectedCheckpointUseCaseImpl): GetCurrentSelectedCheckpointUseCase


    @Binds
    @ActivityScoped
    fun bindSubscribeToDistanceUpdateUseCase(impl: SubscribeToDistanceUpdateUseCaseImpl): SubscribeToDistanceUpdateUseCase

    @Binds
    @ActivityScoped
    fun bindSubscribeToRunnersUpdateUseCase(impl: SubscribeToRunnersUpdateUseCaseImpl): SubscribeToRunnersUpdateUseCase

    @Binds
    @ActivityScoped
    fun bindSaveCurrentSelectedCheckpointUseCase(impl: SaveCurrentSelectedCheckpointUseCaseImpl): SaveCurrentSelectedCheckpointUseCase

    @Binds
    @ActivityScoped
    fun bindOffTrackRunnerUseCase(impl: OffTrackRunnerUseCaseImpl): OffTrackRunnerUseCase

    @Binds
    @ActivityScoped
    fun bindProvideDistanceUseCase(impl: ProvideDistanceUseCaseImpl): ProvideDistanceUseCase

}


@Module
@InstallIn(ActivityComponent::class)
interface RepositoriesModule {

    @Binds
    @ActivityScoped
    fun bindDistanceStatisticRepository(impl: DistanceStatisticRepositoryImpl): DistanceStatisticRepository


    @Binds
    @ActivityScoped
    fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

    @Binds
    @ActivityScoped
    fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener

    @Binds
    @ActivityScoped
    fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository


    @Binds
    @ActivityScoped
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @ActivityScoped
    fun bindRaceRepository(impl: RaceRepositoryImpl): RaceRepository


    @Binds
    @ActivityScoped
    fun bindDistanceRepository(impl: DistanceRepositoryImpl): DistanceRepository


}