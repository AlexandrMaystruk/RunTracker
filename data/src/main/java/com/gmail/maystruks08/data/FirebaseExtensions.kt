package com.gmail.maystruks08.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

data class QueryResponse(val packet: QuerySnapshot?, val error: FirebaseFirestoreException?)

suspend fun Query.awaitRealtime() = suspendCancellableCoroutine<QueryResponse> { continuation ->
    addSnapshotListener { value, error ->
        if (error == null && continuation.isActive) {
            continuation.resume(QueryResponse(value, null))
        }  else if (error != null && continuation.isActive) {
            continuation.resume(QueryResponse(null, error))
        }
    }
}
