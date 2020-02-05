package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.domain.entities.Runner
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

interface FirestoreApi{

    suspend fun getCheckpointsSettings(clientId: String): Task<QuerySnapshot>

    suspend fun updateRunner(runner: Runner): Task<Void>

    suspend fun downloadAllRunners(): Task<QuerySnapshot>

    suspend fun registerSnapshotListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    suspend fun unregisterUpdatesListener()

}