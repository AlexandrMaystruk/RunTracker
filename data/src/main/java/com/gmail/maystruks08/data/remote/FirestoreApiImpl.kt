package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.mappers.toFirestoreCheckpoint
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

    override suspend fun saveCheckpoints(checkpoints: List<CheckpointImpl>) {
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

}