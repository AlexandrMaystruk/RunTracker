package com.gmail.maystruks08.data.remote

import com.gmail.maystruks08.data.remote.pojo.DistanceCheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.data.remote.pojo.RunnerPojo
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Statistic
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface Api{

    suspend fun subscribeToRaceCollectionChange(): Flow<List<Change<RacePojo>>>

    suspend fun subscribeToDistanceCollectionChange(raceId: String): Flow<List<Change<DistancePojo>>>

    suspend fun subscribeToRunnerCollectionChange(raceId: String): Flow<ApiImpl.RunnerChanges>


    //RACE
    suspend fun saveRace(racePojo: RacePojo)


    //DISTANCE
    suspend fun saveDistance(distancePojo: DistancePojo)

    suspend fun updateDistanceRunners(distanceId: String, runnerIds: List<String>)

    suspend fun updateDistanceStatistic(distanceId: String, distanceStatistic: Statistic)

    suspend fun updateDistanceName(distanceId: String, newName: String)

    suspend fun updateDistanceStartDate(distanceId: String, startDate: String?)



    //RUNNER
    suspend fun saveRunner(runnerPojo: RunnerPojo)


    //CHECKPOINTS
    suspend fun getCheckpoints(distanceId: String): DocumentSnapshot

    suspend fun saveDistanceCheckpoints(distanceId: String, checkpoints: List<DistanceCheckpointPojo>)


    suspend fun saveCheckpoints(distanceId: String, checkpoints: List<Checkpoint>)

    suspend fun deleteDistanceCheckpoints(distanceId: String)


    suspend fun getCheckpointsSelectionState(userId: String): DocumentSnapshot

    /**
     * Document id is userId, field name is distanceId, value is selectedCheckpointId
     */
    suspend fun saveCheckpointsSelectionState(userId: String, distanceId: String, selectedCheckpointId: String)


}