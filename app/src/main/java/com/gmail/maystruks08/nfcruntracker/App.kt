//package com.gmail.maystruks08.nfcruntracker
//
//import android.app.Application
//import com.gmail.maystruks08.nfcruntracker.core.di.AndroidModule
//import com.gmail.maystruks08.nfcruntracker.core.di.AppComponent
//
//class App : Application() {
//
//    companion object {
//
//        lateinit var appComponent: AppComponent
//
//        var allMenuComponent: AllMenuComponent? = null
//            get() {
//                if (field == null)
//                    field = appComponent.allMenuComponent()
//                return field
//            }
//
//
//
//        fun clearMenuComponent() {
//            menuComponent = null
//        }
//
//
//    }
//
//
//    override fun onCreate() {
//        super.onCreate()
//
//        appComponent = DaggerAppComponent
//            .builder()
//            .androidModule(AndroidModule(this))
//            .build()
//        appComponent.inject(this)
//    }
//}