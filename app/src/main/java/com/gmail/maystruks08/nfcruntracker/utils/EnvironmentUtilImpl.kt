package com.gmail.maystruks08.nfcruntracker.utils

import com.gmail.maystruks08.domain.repository.EnvironmentUtil
import com.gmail.maystruks08.nfcruntracker.BuildConfig
import javax.inject.Inject

class EnvironmentUtilImpl @Inject constructor(): EnvironmentUtil {

    override suspend fun getCurrentAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

}

