package com.gmail.maystruks08.nfcruntracker.core.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface ViewModelModule {

    @Binds
    fun provideViewModelFactory(impl: DaggerViewModelFactory): ViewModelProvider.Factory

}