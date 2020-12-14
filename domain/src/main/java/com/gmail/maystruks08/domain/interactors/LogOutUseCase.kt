package com.gmail.maystruks08.domain.interactors

interface LogOutUseCase {

    suspend fun revokeAccess(): Boolean

    suspend fun logout(): Boolean

}