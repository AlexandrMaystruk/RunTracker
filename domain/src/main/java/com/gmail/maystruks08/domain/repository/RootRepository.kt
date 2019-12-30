package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Runner


interface RootRepository {

    suspend fun getAllRunners(): List<Runner>
}