package com.gmail.maystruks08.nfcruntracker.core.di.runners

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class RunnersModule {

    @Binds
    @RunnersScope
    abstract fun bindRunnersViewModel(viewModel: RunnersViewModel): ViewModel

}