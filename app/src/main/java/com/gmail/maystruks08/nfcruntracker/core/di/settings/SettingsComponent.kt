package com.gmail.maystruks08.nfcruntracker.core.di.settings

import com.gmail.maystruks08.nfcruntracker.ui.settings.SettingsFragment
import dagger.Subcomponent

@Subcomponent(modules = [SettingsModule::class])
@SettingsScope
interface SettingsComponent {

    fun inject(fragment: SettingsFragment)

}