package com.gmail.maystruks08.nfcruntracker.core.di.runners.runner

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RunnerModule {

    @IntoMap
    @Binds
    @RunnerScope
    @ViewModelKey(RunnerViewModel::class)
    abstract fun bindRunnerViewModel(viewModel: RunnerViewModel): ViewModel
}