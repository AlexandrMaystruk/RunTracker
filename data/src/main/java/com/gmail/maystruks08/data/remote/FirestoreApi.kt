package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.domain.entities.Result
import com.google.firebase.firestore.DocumentSnapshot

interface FirestoreApi{

    suspend fun uploadNewRunner() : Result<Exception, Unit>

    suspend fun downloadAllRunnersData() : Result<Exception, DocumentSnapshot>

    suspend fun markCheckpointAsPassed() : Result<Exception, Unit>

}