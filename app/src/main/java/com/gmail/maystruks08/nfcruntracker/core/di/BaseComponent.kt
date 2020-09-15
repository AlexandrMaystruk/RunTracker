package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import android.content.res.Resources
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.HostActivity
import com.gmail.maystruks08.nfcruntracker.core.di.login.LoginComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.root.RootRunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsComponent
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersWorker
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DatabaseModule::class, HostModule::class, SyncModule::class,  NavigationModule::class, SettingsModule::class, NetworkModule::class])
interface BaseComponent {

    fun provideAppContext(): Context

    fun provideResources(): Resources

    fun provideRootRunnersComponent(): RootRunnersComponent

    fun provideSettingsComponent(): SettingsComponent

    fun provideLoginComponent(): LoginComponent

    fun inject(app: App)

    fun inject(activity: HostActivity)

    fun inject(workHelper: SyncRunnersWorker)

}