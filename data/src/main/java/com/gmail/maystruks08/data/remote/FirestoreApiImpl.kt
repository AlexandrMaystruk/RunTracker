package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.mappers.fromFirestoreRunner
import com.gmail.maystruks08.data.mappers.toFirestoreCheckpoint
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class FirestoreApiImpl @Inject constructor(private val db: FirebaseFirestore) : FirestoreApi {

    companion object {

        private const val RUNNER_COLLECTION= "runners"

        private const val SETTINGS_COLLECTION= "settings"


        private const val START_DATE_DOCUMENT_NAME = "start_date"

        private const val ADMIN_USER_IDS_DOCUMENT_NAME = "adminUsers"


        private const val CHECKPOINTS_DOCUMENT_NAME = "checkpoints"

    }

    override suspend fun getCheckpoints(): Task<DocumentSnapshot> = db.collection(SETTINGS_COLLECTION).document(CHECKPOINTS_DOCUMENT_NAME).get()

    override suspend fun saveCheckpoints(checkpoints: List<Checkpoint>) {
        val document = db.collection(SETTINGS_COLLECTION).document(CHECKPOINTS_DOCUMENT_NAME)
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


    override suspend fun getCheckpointsSettings(clientId: String): Task<DocumentSnapshot> = db.collection(SETTINGS_COLLECTION).document(clientId).get()

    override suspend fun getAdminUserIds(): Task<DocumentSnapshot> {
       return db.collection(SETTINGS_COLLECTION).document(ADMIN_USER_IDS_DOCUMENT_NAME).get()
    }

    override suspend fun saveCheckpointsSettings(clientId: String, config: SettingsRepository.Config) {
        if (config.checkpointId != null || config.checkpointIronPeopleId != null) {
            val document = db.collection(SETTINGS_COLLECTION).document(clientId)
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
        val document = db.collection(SETTINGS_COLLECTION).document(START_DATE_DOCUMENT_NAME)
        try {
            awaitTaskCompletable(document.update("Start at", date))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(document.set(mutableMapOf("Start at" to date)))
        }
    }

    override suspend fun getDateOfStart(): Task<DocumentSnapshot> =
        db.collection(SETTINGS_COLLECTION).document(START_DATE_DOCUMENT_NAME).get()

    override suspend fun updateRunner(runner: Runner): Task<Void> =
        db.collection(RUNNER_COLLECTION).document(runner.number.toString()).set(runner.toFirestoreRunner())

    @ExperimentalCoroutinesApi
    override suspend fun subscribeToRunnerDataRealtimeUpdates(): Flow<List<RunnerChange>>{
        return channelFlow<List<RunnerChange>> {
            val eventDocument = db.collection(RUNNER_COLLECTION)
            // 1) Register callback to the API
            val subscription = eventDocument.addSnapshotListener { snapshots, e ->
                Timber.i("Snapshot size = ${snapshots?.documentChanges?.size}")
                snapshots?.documentChanges?.map { doc ->
                    val changeType = when (doc.type) {
                        DocumentChange.Type.ADDED -> Change.ADD
                        DocumentChange.Type.MODIFIED -> Change.UPDATE
                        DocumentChange.Type.REMOVED -> Change.REMOVE
                    }
                    RunnerChange(doc.document.toObject(RunnerPojo::class.java).fromFirestoreRunner(), changeType)
                }?.let{
                    // 2) Send items to the Flow
                    channel.offer(it)
                }
            }
            // 3) Don't close the stream of data, keep it open until the consumer
            // stops listening or the API calls onCompleted or onError.
            // When that happens, cancel the subscription to the 3P library
            awaitClose {
                subscription.remove()
                Timber.e("Chanel closed")
            }
        }.flowOn(Dispatchers.Main)
    }
}