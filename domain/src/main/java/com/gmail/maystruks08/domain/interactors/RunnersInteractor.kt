package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner

interface RunnersInteractor {

    suspend fun getAllRunners(): ResultOfTask<Exception, List<Runner>>

    suspend fun updateRunnersCache(onResult: (ResultOfTask<Exception, List<Runner>>) -> Unit)

    suspend fun addCurrentCheckpointToRunner(cardId: String): Runner?

}