package com.gmail.maystruks08.nfcruntracker.core.di

import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
object NavigationModule {

    @JvmStatic
    @Provides
    @Singleton
    fun cicerone(): Cicerone<Router> = Cicerone.create()

    @JvmStatic
    @Provides
    @Singleton
    fun router(cicerone: Cicerone<Router>): Router = cicerone.router

    @JvmStatic
    @Provides
    @Singleton
    fun navigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder = cicerone.navigatorHolder

}