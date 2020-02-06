package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import android.content.res.Resources
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.HostActivity
import com.gmail.maystruks08.nfcruntracker.core.di.login.LoginComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runner.RunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.RunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DatabaseModule::class,  NavigationModule::class, SettingsModule::class])
interface BaseComponent {

    fun provideAppContext(): Context

    fun provideResources(): Resources

    fun provideRunnersComponent(): RunnersComponent

    fun provideRunnerComponent(): RunnerComponent

    fun provideSettingsComponent(): SettingsComponent

    fun provideLoginComponent(): LoginComponent

    fun inject(app: App)

    fun inject(activity: HostActivity)

}