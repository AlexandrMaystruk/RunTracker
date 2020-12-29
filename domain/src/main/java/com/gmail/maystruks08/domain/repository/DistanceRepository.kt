package com.gmail.maystruks08.domain.repository

import com.gmail.maystruks08.domain.entities.Distance

interface DistanceRepository {

    suspend fun getDistanceList(): List<Distance>


}