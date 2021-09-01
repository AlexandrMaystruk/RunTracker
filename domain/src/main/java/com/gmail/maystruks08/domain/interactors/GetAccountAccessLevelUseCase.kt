package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.account.AssesLevel

interface GetAccountAccessLevelUseCase {

    suspend fun invoke(): AssesLevel

}