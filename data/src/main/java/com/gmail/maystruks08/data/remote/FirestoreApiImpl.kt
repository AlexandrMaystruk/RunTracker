package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.domain.entities.Result
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirestoreApiImpl @Inject constructor(private val firestore: FirebaseFirestore) : FirestoreApi {

    override suspend fun uploadNewRunner(): Result<Exception, Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun downloadAllRunnersData(): Result<Exception, DocumentSnapshot> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun markCheckpointAsPassed(): Result<Exception, Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}