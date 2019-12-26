package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.ui.HostActivity
import dagger.Component
import javax.inject.Singleton

@Component (modules = [AndroidModule::class, DataAccessModule::class])
@Singleton
interface AppComponent {

    fun inject(app: App)

    fun inject(mainActivity: HostActivity)

}