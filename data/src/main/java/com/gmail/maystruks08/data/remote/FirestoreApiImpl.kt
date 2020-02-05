package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.domain.entities.Runner
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirestoreApiImpl @Inject constructor(private val db: FirebaseFirestore) : FirestoreApi {

    private var registration: ListenerRegistration?= null

    override suspend fun getCheckpointsSettings(clientId: String): Task<QuerySnapshot> { TODO("not implemented") }

    override suspend fun updateRunner(runner: Runner): Task<Void>  =db.collection("runners").document(runner.id).set(runner)

    override suspend fun downloadAllRunners(): Task<QuerySnapshot> = db.collection("runners").get()

    override suspend fun registerSnapshotListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        registration = db.collection("runners").addSnapshotListener { snapshots, e ->
            listener.invoke(snapshots, e)
        }
    }

    override suspend fun unregisterUpdatesListener() {
        registration?.remove()
    }
}