package com.gmail.maystruks08.nfcruntracker.ui.login

interface LogOutUseCase {

    suspend fun revokeAccess(): Boolean

    suspend fun logout(): Boolean

}