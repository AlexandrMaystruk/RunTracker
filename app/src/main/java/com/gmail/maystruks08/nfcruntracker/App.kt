package com.gmail.maystruks08.nfcruntracker

import android.app.Application
import com.gmail.maystruks08.nfcruntracker.core.di.AppModule
import com.gmail.maystruks08.nfcruntracker.core.di.BaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.DaggerBaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.root.RootComponent

class App : Application() {

    companion object {

        lateinit var baseComponent: BaseComponent

        var rootComponent: RootComponent? = null
            get() {
                if (field == null)
                    field = baseComponent.provideRootComponent()
                return field
            }

        fun clearRootComponent(){
            rootComponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        baseComponent = DaggerBaseComponent
            .builder()
            .appModule(AppModule(this))
            .build()
        baseComponent.inject(this)
    }
}