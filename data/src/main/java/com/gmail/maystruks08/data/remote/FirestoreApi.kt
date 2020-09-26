package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import java.util.*

interface FirestoreApi{

    data class Integer(val number: Int?)

    suspend fun getCheckpoints(): Task<DocumentSnapshot>

    suspend fun saveCheckpoints(checkpoints: List<Checkpoint>)

    suspend fun getCheckpointsSettings(clientId: String): Task<DocumentSnapshot>

    suspend fun getAdminUserIds(): Task<DocumentSnapshot>

    suspend fun saveCheckpointsSettings(clientId: String, config: SettingsRepository.Config)

    suspend fun saveDateOfStart(date: Date)

    suspend fun getDateOfStart(): Task<DocumentSnapshot>

    suspend fun updateRunner(runner: Runner): Task<Void>

    suspend fun subscribeToRunnerDataRealtimeUpdates(): Flow<List<RunnerChange>>

}