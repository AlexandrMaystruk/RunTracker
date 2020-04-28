package com.gmail.maystruks08.nfcruntracker.core.di.runners

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RunnersModule {

    @IntoMap
    @Binds
    @RunnersScope
    @ViewModelKey(RunnersViewModel::class)
    abstract fun bindRunnersViewModel(viewModel: RunnersViewModel): ViewModel

}