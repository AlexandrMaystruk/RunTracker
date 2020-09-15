package com.gmail.maystruks08.nfcruntracker.core.di.host

import com.gmail.maystruks08.nfcruntracker.HostActivity
import com.gmail.maystruks08.nfcruntracker.core.di.NavigationModule
import com.gmail.maystruks08.nfcruntracker.core.di.SettingsModule
import com.gmail.maystruks08.nfcruntracker.core.di.SyncModule
import com.gmail.maystruks08.nfcruntracker.core.di.login.LoginComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.root.RootRunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsComponent
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersWorker
import dagger.Subcomponent

@Subcomponent(modules = [HostModule::class, NavigationModule::class, SyncModule::class, SettingsModule::class])
@HostScope
interface HostComponent {

    fun provideRootRunnersComponent(): RootRunnersComponent

    fun provideSettingsComponent(): SettingsComponent

    fun provideLoginComponent(): LoginComponent

    fun inject(activity: HostActivity)

    fun inject(workHelper: SyncRunnersWorker)

}