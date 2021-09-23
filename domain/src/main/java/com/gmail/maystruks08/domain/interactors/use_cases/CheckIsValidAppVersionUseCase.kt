package com.gmail.maystruks08.domain.interactors.use_cases

interface CheckIsValidAppVersionUseCase {

    suspend fun invoke(): Boolean

}