package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.data.repository.RegisterNewRunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractorImpl
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class RegisterNewRunnerModule {

    @Binds
    @ActivityScoped
    abstract fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository

    @Binds
    @ActivityScoped
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerInteractor

}

