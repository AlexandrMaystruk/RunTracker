package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.RegisterNewRunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractorImpl
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@InstallIn(FragmentComponent::class)
@Module
abstract class RegisterFragmentModule {

    @Binds
    @FragmentScoped
    abstract fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository

    @Binds
    @FragmentScoped
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerInteractor

}


@InstallIn(FragmentComponent::class)
@Module
abstract class CheckpointEditorFragmentModule {

    

}
