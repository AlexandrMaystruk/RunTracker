package com.gmail.maystruks08.domain.interactors

import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.repository.RootRepository
import javax.inject.Inject


class RootInteractorImpl @Inject constructor(private val rootRepository: RootRepository) : RootInteractor{

    override suspend fun getAllRunners(): List<Runner>{
        return rootRepository.getAllRunners()
    }
}