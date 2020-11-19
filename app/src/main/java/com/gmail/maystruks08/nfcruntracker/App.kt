package com.gmail.maystruks08.nfcruntracker

import android.app.Application
import com.gmail.maystruks08.nfcruntracker.core.di.AppModule
import com.gmail.maystruks08.nfcruntracker.core.di.BaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.DaggerBaseComponent
import com.gmail.maystruks08.nfcruntracker.core.di.host.HostComponent
import com.gmail.maystruks08.nfcruntracker.core.di.login.LoginComponent
import com.gmail.maystruks08.nfcruntracker.core.di.register.RegisterNewRunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.RunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.result.RunnersResultComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.runner.RunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.settings.SettingsComponent
import com.gmail.maystruks08.nfcruntracker.utils.TimberFileTree
import timber.log.Timber

class App : Application() {

    companion object {


        lateinit var baseComponent: BaseComponent

        var hostComponent: HostComponent? = null
            get() {
                if (field == null)
                    field = baseComponent.provideHostComponent()
                return field
            }




        var loginComponent: LoginComponent? = null
            get() {
                if (field == null)
                    field = hostComponent?.provideLoginComponent()
                return field
            }

        var settingsComponent: SettingsComponent? = null
            get() {
                if (field == null)
                    field = hostComponent?.provideSettingsComponent()
                return field
            }


        var registerNewRunnerComponent: RegisterNewRunnerComponent? = null
            get() {
                if (field == null)
                    field = runnersComponent?.provideRegisterRunnerComponent()
                return field
            }


        var runnersComponent: RunnersComponent? = null
            get() {
                if (field == null)
                    field = hostComponent?.provideRunnersComponent()
                return field
            }



        var runnerComponent: RunnerComponent? = null
            get() {
                if (field == null)
                    field = runnersComponent?.provideRunnerComponent()
                return field
            }

        var runnersResultComponent: RunnersResultComponent? = null
            get() {
                if (field == null)
                    field = runnersComponent?.provideRunnerResultComponent()
                return field
            }

        fun clearHostComponent() {
            hostComponent = null
        }

        fun clearRunnersComponent() {
            runnersComponent = null
        }

        fun clearRunnerComponent() {
            runnerComponent = null
        }

        fun clearRunnersResultComponent() {
            runnersResultComponent = null
        }

        fun clearSettingsComponent() {
            settingsComponent = null
        }

        fun clearLoginComponent() {
            loginComponent = null
        }

        fun clearRegisterNewRunnerComponent() {
            registerNewRunnerComponent = null
        }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(TimberFileTree(this))

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        baseComponent = DaggerBaseComponent
            .builder()
            .appModule(AppModule(this))
            .build()
        baseComponent.inject(this)
    }
}