package com.gmail.maystruks08.nfcruntracker.core.di.register

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class RegisterNewRunnerModule {

    @IntoMap
    @Binds
    @RegisterScope
    @ViewModelKey(RegisterNewRunnerViewModel::class)
    abstract fun bindRegisterViewModel(viewModel: RegisterNewRunnerViewModel): ViewModel
}
