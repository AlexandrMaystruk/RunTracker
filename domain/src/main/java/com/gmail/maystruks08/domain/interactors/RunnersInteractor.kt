package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Runner


interface RunnersInteractor {

    suspend fun getAllRunners(): List<Runner>

    suspend fun getFilteredRunners(filter: String): List<Runner>

    suspend fun addCurrentCheckpointToRunner(cardId: String): Runner?

}