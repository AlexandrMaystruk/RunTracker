package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface Api{

    suspend fun subscribeToRaceCollectionChange(): Flow<List<Change<RacePojo>>>

    suspend fun subscribeToDistanceCollectionChange (): Flow<List<Change<DistancePojo>>>

    suspend fun subscribeToRunnerCollectionChange(): Flow<List<Change<RunnerPojo>>>


    //RACE
    suspend fun saveRace(racePojo: RacePojo)


    //DISTANCE
    suspend fun saveDistance(distancePojo: DistancePojo)


    //RUNNER
    suspend fun saveRunner(runnerPojo: RunnerPojo)


    //CHECKPOINTS
    suspend fun getCheckpoints(raceId: String, distanceId: String): DocumentSnapshot

    suspend fun saveCheckpoints(raceId: String, distanceId: String, checkpoints: List<Checkpoint>)


    suspend fun getCheckpointsSelectionState(userId: String): DocumentSnapshot

    /**
     * Document id is userId, field name is distanceId, value is selectedCheckpointId
     */
    suspend fun saveCheckpointsSelectionState(userId: String, distanceId: String, selectedCheckpointId: String)


}