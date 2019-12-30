package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.repository.RootRepository
import javax.inject.Inject

class RootRepositoryImpl @Inject constructor(): RootRepository {

    override suspend fun getAllRunners() = mutableListOf<Runner>().apply {
        add(Runner("id", "Ann", "Golodygina",20))
        add(Runner("id", "Alexandr", "Maystruk",22))
    }
}