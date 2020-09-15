package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.nfcruntracker.core.di.host.HostScope
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

@Module
object NavigationModule {

    @JvmStatic
    @Provides
    @HostScope
    fun cicerone(): Cicerone<Router> = Cicerone.create()

    @JvmStatic
    @Provides
    @HostScope
    fun router(cicerone: Cicerone<Router>): Router = cicerone.router

    @JvmStatic
    @Provides
    @HostScope
    fun navigatorHolder(cicerone: Cicerone<Router>): NavigatorHolder = cicerone.navigatorHolder

}