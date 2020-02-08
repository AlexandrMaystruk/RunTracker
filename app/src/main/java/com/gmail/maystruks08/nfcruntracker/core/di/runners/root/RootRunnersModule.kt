package com.gmail.maystruks08.nfcruntracker.core.di.runners.root

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
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

}