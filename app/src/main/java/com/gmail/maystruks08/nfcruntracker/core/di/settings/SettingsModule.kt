package com.gmail.maystruks08.nfcruntracker.core.di.settings

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.repository.SettingsRepositoryImpl
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.ViewModelModule
import com.gmail.maystruks08.nfcruntracker.ui.settings.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule::class])
abstract class SettingsModule {

    @IntoMap
    @Binds
    @SettingsScope
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @SettingsScope
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

}