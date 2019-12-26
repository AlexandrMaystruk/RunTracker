package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidModule::class, DataAccessModule::class])
interface CoreComponent {

    fun context(): Context

//    fun sharedPreferences(): SharedPreferences
}