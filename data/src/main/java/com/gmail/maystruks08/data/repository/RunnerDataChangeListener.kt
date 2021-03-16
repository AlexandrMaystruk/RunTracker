package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.domain.entities.TaskResult

interface RunnerDataChangeListener {

    suspend fun getLastSavedRace(): TaskResult<Exception, Pair<String, String>>

}