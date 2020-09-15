package com.gmail.maystruks08.nfcruntracker.core.di.host

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

@Module(includes = [ViewModelModule::class])
abstract class HostModule {

    @IntoMap
    @Binds
    @HostScope
    @ViewModelKey(HostViewModel::class)
    abstract fun bindRunnersViewModel(viewModel: HostViewModel): ViewModel

    @Binds
    @HostScope
    abstract fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener

    @Binds
    @HostScope
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

}