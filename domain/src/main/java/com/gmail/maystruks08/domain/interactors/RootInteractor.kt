package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Runner


interface RootInteractor {

    suspend fun getAllRunners(): List<Runner>
}