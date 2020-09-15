package com.gmail.maystruks08.nfcruntracker.core.di

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.RunnersRepositoryImpl
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.nfcruntracker.HostViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
abstract class HostModule {

    @IntoMap
    @Binds
    @Singleton
    @ViewModelKey(HostViewModel::class)
    abstract fun bindRunnersViewModel(viewModel: HostViewModel): ViewModel

    @Binds
    @Singleton
    abstract fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener

    @Binds
    @Singleton
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

}