package com.gmail.maystruks08.nfcruntracker.core.di.runners.result

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.result.RunnerResultViewModel
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RunnersResultModule {

    @IntoMap
    @Binds
    @RunnersResultsScope
    @ViewModelKey(RunnerViewModel::class)
    abstract fun bindRunnerResultViewModel(viewModel: RunnerResultViewModel): ViewModel
}