package com.gmail.maystruks08.nfcruntracker.core.di.viewmodel

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {

    @Binds
    fun provideViewModelFactory(impl: DaggerViewModelFactory): ViewModelProvider.Factory

}