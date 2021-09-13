package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.awaitTaskResult
import com.gmail.maystruks08.data.mappers.toFirestoreCheckpoint
import com.gmail.maystruks08.data.remote.pojo.DistanceCheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.data.serializeToMap
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.DistanceStatistic
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber
import javax.inject.Inject


@ExperimentalCoroutinesApi
class ApiImpl @Inject constructor(private val db: FirebaseFirestore) : Api {

    companion object {
        private const val RACES_COLLECTION = "races"
        private const val DISTANCES_COLLECTION = "distances"
        private const val RUNNER_COLLECTION = "runner_ref"
        private const val CHECKPOINTS_COLLECTION = "checkpoints"
        private const val SETTINGS_COLLECTION = "settings"
    }

    override suspend fun subscribeToRaceCollectionChange(): Flow<List<Change<RacePojo>>> {
        return channelFlow {
            val eventDocument = db.collection(RACES_COLLECTION)
            val subscription = eventDocument.addSnapshotListener { snapshots, _ ->
                Timber.i("Snapshot size = ${snapshots?.documentChanges?.size}")
                val raceChanges = snapshots?.documentChanges?.map {
                    Change(it.document.toObject(RacePojo::class.java), it.getChangeType())
                }.orEmpty()
                channel.offer(raceChanges)
            }
            awaitClose {
                subscription.remove()
                Timber.e("Chanel race updates closed")
            }
        }
    }

    override suspend fun subscribeToDistanceCollectionChange(raceId: String): Flow<List<Change<DistancePojo>>> {
        return channelFlow {
            val eventDocument = db.collection(DISTANCES_COLLECTION)
            val subscription = eventDocument.whereEqualTo("raceId", raceId).addSnapshotListener { snapshots, _ ->
                Timber.i("Snapshot size = ${snapshots?.documentChanges?.size}")
                val distancesChanges = snapshots?.documentChanges?.map {
                    Change(it.document.toObject(DistancePojo::class.java), it.getChangeType())
                }.orEmpty()
                channel.offer(distancesChanges)
            }
            awaitClose {
                subscription.remove()
                Timber.e("Chanel distance updates closed")
            }
        }
    }


    @ExperimentalCoroutinesApi
    override suspend fun subscribeToRunnerCollectionChange(raceId: String): Flow<List<Change<RunnerPojo>>> {
        return channelFlow {
            val eventDocument = db.collection(RUNNER_COLLECTION).whereEqualTo("actualRaceId", raceId)
            val subscription = eventDocument.addSnapshotListener { snapshots, _ ->
                Timber.i("Snapshot size = ${snapshots?.documentChanges?.size}")
                val runnersChanges = snapshots?.documentChanges?.map {
                    Timber.e(it.document.data.toString())
                    Change(it.document.toObject(RunnerPojo::class.java), it.getChangeType())
                }.orEmpty()
                channel.offer(runnersChanges)
            }
            awaitClose {
                subscription.remove()
                Timber.e("Chanel runner updates closed")
            }
        }
    }

    override suspend fun saveRace(racePojo: RacePojo) {
        val raceDocument = db.collection(RACES_COLLECTION).document(racePojo.id.replaceSpecialSymbols())
        awaitTaskCompletable(raceDocument.set(racePojo))
    }

    override suspend fun saveDistance(distancePojo: DistancePojo) {
        val distanceDocument = db.collection(DISTANCES_COLLECTION).document(distancePojo.id.replaceSpecialSymbols())
        awaitTaskCompletable(distanceDocument.set(distancePojo))
    }

    override suspend fun updateDistanceRunners(distanceId: String, runnerIds: List<String>) {
        val distanceDocument = db.collection(DISTANCES_COLLECTION).document(distanceId.replaceSpecialSymbols())
        awaitTaskCompletable(distanceDocument.update("runnerIds", runnerIds))
    }

    override suspend fun updateDistanceStatistic(
        distanceId: String,
        distanceStatistic: DistanceStatistic
    ) {
        val distanceDocument = db.collection(DISTANCES_COLLECTION).document(distanceId.replaceSpecialSymbols())
        awaitTaskCompletable(
            distanceDocument.update(
                mapOf(
                    "finisherCount" to distanceStatistic.finisherCount,
                    "runnerCountOffTrack" to distanceStatistic.runnerCountOffTrack,
                    "runnerCountInProgress" to distanceStatistic.runnerCountInProgress
                )
            )
        )
    }

    override suspend fun updateDistanceName(distanceId: String, newName: String) {
        val distanceDocument =
            db.collection(DISTANCES_COLLECTION).document(distanceId.replaceSpecialSymbols())
        awaitTaskCompletable(
            distanceDocument.update(
                mapOf(
                    "name" to newName,
                )
            )
        )
    }


    override suspend fun saveRunner(runnerPojo: RunnerPojo) {
        val runnerDocument =
            db.collection(RUNNER_COLLECTION).document(runnerPojo.number.replaceSpecialSymbols())
        return try {
            awaitTaskCompletable(runnerDocument.update(runnerPojo.serializeToMap()))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(runnerDocument.set(runnerPojo))
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    override suspend fun saveCheckpoints(
        distanceId: String,
        checkpoints: List<Checkpoint>
    ) {
        val document = db.collection(CHECKPOINTS_COLLECTION).document(distanceId.replaceSpecialSymbols())
        val map = hashMapOf<String, Any>()
            .apply {
                checkpoints.forEach { this[it.getId()] = it.toFirestoreCheckpoint() }
            }
        return try {
            awaitTaskCompletable(document.update(map))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(document.set(map))
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    override suspend fun deleteDistanceCheckpoints(distanceId: String) {
        val document = db.collection(CHECKPOINTS_COLLECTION).document(distanceId.replaceSpecialSymbols())
        awaitTaskCompletable(document.delete())
    }

    override suspend fun saveDistanceCheckpoints(
        distanceId: String,
        checkpoints: List<DistanceCheckpointPojo>
    ) {
        val document =
            db.collection(CHECKPOINTS_COLLECTION).document(distanceId.replaceSpecialSymbols())
        val map = hashMapOf<String, Any>().apply { checkpoints.forEach { this[it.id] = it } }
        return try {
            awaitTaskCompletable(document.update(map))
        } catch (e: FirebaseFirestoreException) {
            awaitTaskCompletable(document.set(map))
        } catch (e: Exception){
            Timber.e(e)
            throw e
        }
    }

    override suspend fun getCheckpoints(
        distanceId: String
    ): DocumentSnapshot {
        return awaitTaskResult(db.collection(CHECKPOINTS_COLLECTION).document(distanceId.replaceSpecialSymbols()).get())
    }

    override suspend fun getCheckpointsSelectionState(userId: String): DocumentSnapshot {
        return awaitTaskResult(db.collection(SETTINGS_COLLECTION).document(userId).get())
    }


    override suspend fun saveCheckpointsSelectionState(
        userId: String,
        distanceId: String,
        selectedCheckpointId: String
    ) {
        val document = db.collection(SETTINGS_COLLECTION).document(userId)
        return awaitTaskCompletable(document.update(distanceId, selectedCheckpointId))
    }

    private fun DocumentChange.getChangeType(): ModifierType = when (type) {
        DocumentChange.Type.ADDED -> ModifierType.ADD
        DocumentChange.Type.MODIFIED -> ModifierType.UPDATE
        DocumentChange.Type.REMOVED -> ModifierType.REMOVE
    }

    private fun String.replaceSpecialSymbols(): String{
        return replace("/", "_")
    }
}