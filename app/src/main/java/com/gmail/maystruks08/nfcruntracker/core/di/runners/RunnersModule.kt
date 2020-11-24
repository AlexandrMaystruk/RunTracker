package com.gmail.maystruks08.nfcruntracker.core.di.runners

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractorImpl
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RunnersModule {

    @IntoMap
    @Binds
    @RunnersScope
    @ViewModelKey(RunnersViewModel::class)
    abstract fun bindRunnerViewModel(viewModel: RunnersViewModel): ViewModel

    @Binds
    @RunnersScope
    abstract fun bindRunnersInteractor(impl: RunnersInteractorImpl): RunnersInteractor

}