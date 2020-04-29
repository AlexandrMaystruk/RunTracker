package com.gmail.maystruks08.nfcruntracker.core.di.runners.root

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.RunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractorImpl
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.runners.RootRunnersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RootRunnersModule {

    @IntoMap
    @Binds
    @RootRunnersScope
    @ViewModelKey(RootRunnersViewModel::class)
    abstract fun bindViewModel(viewModel: RootRunnersViewModel): ViewModel

    @Binds
    @RootRunnersScope
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

    @Binds
    @RootRunnersScope
    abstract fun bindRunnersInteractor(impl: RunnersInteractorImpl): RunnersInteractor

}