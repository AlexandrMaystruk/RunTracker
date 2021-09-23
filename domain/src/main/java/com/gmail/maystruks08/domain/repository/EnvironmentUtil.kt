package com.gmail.maystruks08.domain.repository

interface EnvironmentUtil {

    suspend fun getCurrentAppVersion(): String

}