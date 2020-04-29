package com.gmail.maystruks08.nfcruntracker.core.di.settings

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelModule
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

}