package com.gmail.maystruks08.data

import com.gmail.maystruks08.domain.LogHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogHelperImpl @Inject constructor() : LogHelper {

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }
}