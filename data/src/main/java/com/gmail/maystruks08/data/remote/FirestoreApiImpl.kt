package com.gmail.maystruks08.data.remote

import android.util.Log
import com.gmail.maystruks08.data.awaitRealtime
import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.data.mappers.fromFirestoreRunner
import com.gmail.maystruks08.data.mappers.toFirestoreCheckpoint
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.serializeToMap
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class FirestoreApiImpl @Inject constructor(private val db: FirebaseFirestore) : FirestoreApi {

    private var registration: ListenerRegistration? = null

    companion object {

        private const val START_DATE_DOCUMENT_NAME = "start_date"

        private const val CHECKPOINTS_DOCUMENT_NAME = "checkpoints"

    }

    override suspend fun getCheckpoints(): Task<DocumentSnapshot> = db.collection("settings").document(CHECKPOINTS_DOCUMENT_NAME).get()

    override suspend fun saveCheckpoints(checkpoints: List<Checkpoint>) {
        val document = db.collection("settings").document(CHECKPOINTS_DOCUMENT_NAME)
        val map = hashMapOf<String, Any>()
            .apply {
                checkpoints.forEachIndexed { index, checkpoint ->
                    this[index.toString()] = checkpoint.toFirestoreCheckpoint()
                }
            }
        try {
            awaitTaskCompletable(document.update(map))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(document.set(map))
        }
    }


    override suspend fun getCheckpointsSettings(clientId: String): Task<DocumentSnapshot> = db.collection("settings").document(clientId).get()

    override suspend fun saveCheckpointsSettings(clientId: String, config: SettingsRepository.Config) {
        if (config.checkpointId != null || config.checkpointIronPeopleId != null) {
            val document = db.collection("settings").document(clientId)
            if (config.checkpointId != null) {
                 try {
                    awaitTaskCompletable(document.update("checkpointId", FirestoreApi.Integer(config.checkpointId)))
                } catch (e: FirebaseFirestoreException) {
                    awaitTaskCompletable(document.set(mutableMapOf("checkpointId" to FirestoreApi.Integer(config.checkpointId))))
                }
            } else if (config.checkpointIronPeopleId != null) {
                 try {
                    awaitTaskCompletable(document.update("checkpointIronPeopleId", FirestoreApi.Integer(config.checkpointIronPeopleId)))
                } catch (e: FirebaseFirestoreException) {
                    awaitTaskCompletable(document.set(mutableMapOf("checkpointIronPeopleId" to FirestoreApi.Integer(config.checkpointIronPeopleId))))
                }
            }
        } else throw Exception("Checkpoint ids is empty")
    }

    override suspend fun saveDateOfStart(date: Date) {
        val document = db.collection("settings").document(START_DATE_DOCUMENT_NAME)
        try {
            awaitTaskCompletable(document.update("Start at", date))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(document.set(mutableMapOf("Start at" to date)))
        }
    }

    override suspend fun getDateOfStart(): Task<DocumentSnapshot> =
        db.collection("settings").document(START_DATE_DOCUMENT_NAME).get()

    override suspend fun updateRunner(runner: Runner): Task<Void> =
        db.collection("runners").document(runner.id).set(runner.toFirestoreRunner())

    override fun subscribeToRunnerDataRealtimeUpdates(listener: (List<RunnerChange>) -> Unit) {
        registration = db.collection("runners").addSnapshotListener { snapshots, e ->
            if ( e == null) {
                snapshots?.documentChanges?.map { doc ->
                    Timber.log(Log.ERROR, doc.document.toObject(RunnerPojo::class.java).toString())
                    val changeType = when (doc.type) {
                        DocumentChange.Type.ADDED -> Change.ADD
                        DocumentChange.Type.MODIFIED -> Change.UPDATE
                        DocumentChange.Type.REMOVED -> Change.REMOVE
                    }
                    RunnerChange(doc.document.toObject(RunnerPojo::class.java).fromFirestoreRunner(), changeType)
                }?.let {
                    listener(it)
                }
            }
            if (e != null) throw e
        }
    }

        override suspend fun unregisterUpdatesListener() {
            registration?.remove()
        }
    }