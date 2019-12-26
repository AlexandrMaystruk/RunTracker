package com.gmail.maystruks08.nfcruntracker

import android.app.Application
import com.gmail.maystruks08.nfcruntracker.core.di.AndroidModule
import com.gmail.maystruks08.nfcruntracker.core.di.AppComponent
import com.gmail.maystruks08.nfcruntracker.core.di.DaggerAppComponent

class App : Application() {

    companion object {

        lateinit var appComponent: AppComponent

    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .androidModule(AndroidModule(this))
            .build()
        appComponent.inject(this)
    }
}