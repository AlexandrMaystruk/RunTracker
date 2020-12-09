package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.RunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.CheckpointInteractorImpl
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractorImpl
import com.gmail.maystruks08.domain.repository.RunnersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

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

}