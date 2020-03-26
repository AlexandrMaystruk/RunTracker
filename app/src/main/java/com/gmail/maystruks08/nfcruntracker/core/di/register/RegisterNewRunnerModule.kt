package com.gmail.maystruks08.nfcruntracker.core.di.register

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.RegisterNewRunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractorImpl
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
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

    @Binds
    @RegisterScope
    abstract fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository

    @Binds
    @RegisterScope
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerInteractor
}
