package com.gmail.maystruks08.nfcruntracker.core.di.checkpoint_editor

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.RegisterNewRunnersRepositoryImpl
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractorImpl
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class CheckpointEditorModule {

    @IntoMap
    @Binds
    @CheckpointEditorScope
    @ViewModelKey(RegisterNewRunnerViewModel::class)
    abstract fun bindRegisterViewModel(viewModel: RegisterNewRunnerViewModel): ViewModel

    @Binds
    @CheckpointEditorScope
    abstract fun bindRegisterRunnersRepository(impl: RegisterNewRunnersRepositoryImpl): RegisterNewRunnersRepository

    @Binds
    @CheckpointEditorScope
    abstract fun bindRegisterNewRunnerInteractor(impl: RegisterNewRunnerInteractorImpl): RegisterNewRunnerInteractor
}
