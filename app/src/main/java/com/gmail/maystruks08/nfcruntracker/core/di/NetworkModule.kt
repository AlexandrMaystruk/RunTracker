package com.gmail.maystruks08.nfcruntracker.core.di

import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.nfcruntracker.utils.NetworkUtilImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkUtil(impl: NetworkUtilImpl): NetworkUtil

}