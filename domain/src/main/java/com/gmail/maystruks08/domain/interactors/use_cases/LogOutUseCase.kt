package com.gmail.maystruks08.domain.interactors.use_cases

interface LogOutUseCase {

    suspend fun revokeAccess(): Boolean

    suspend fun logout(): Boolean

}