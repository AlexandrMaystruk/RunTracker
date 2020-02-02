package com.gmail.maystruks08.nfcruntracker

import android.app.Application
import com.gmail.maystruks08.nfcruntracker.core.di.AppModule
import com.gmail.maystruks08.nfcruntracker.core.di.BaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.DaggerBaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runner.RunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.RunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsComponent

class App : Application() {

    companion object {

        lateinit var baseComponent: BaseComponent

        var runnersComponent: RunnersComponent? = null
            get() {
                if (field == null)
                    field = baseComponent.provideRunnersComponent()
                return field
            }

        var runnerComponent: RunnerComponent? = null
            get() {
                if (field == null)
                    field = baseComponent.provideRunnerComponent()
                return field
            }

        var settingsComponent: SettingsComponent? = null
            get() {
                if (field == null)
                    field = baseComponent.provideSettingsComponent()
                return field
            }

        fun clearRunnersComponent(){
            runnersComponent = null
        }

        fun clearRunnerComponent(){
            runnerComponent = null
        }

        fun clearSettingsComponent(){
            runnersComponent = null
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