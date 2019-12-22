package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule (private val context: Context) {

    @Provides
    @Singleton
    fun context (): Context = context

}