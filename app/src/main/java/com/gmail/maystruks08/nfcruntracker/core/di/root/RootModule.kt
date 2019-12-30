package com.gmail.maystruks08.nfcruntracker.core.di.root

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.RootRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RootInteractor
import com.gmail.maystruks08.domain.interactors.RootInteractorImpl
import com.gmail.maystruks08.domain.repository.RootRepository
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.RootViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RootModule {

    @IntoMap
    @Binds
    @RootScope
    @ViewModelKey(RootViewModel::class)
    abstract fun bindRootViewModel(viewModel: RootViewModel): ViewModel

    @Binds
    @RootScope
    abstract fun bindRootRepository(impl: RootRepositoryImpl): RootRepository

    @Binds
    @RootScope
    abstract fun bindRootInteractor(impl: RootInteractorImpl): RootInteractor

}