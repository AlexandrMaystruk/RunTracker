package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable

@Dao
interface CheckpointDAO : BaseDao<CheckpointTable> {

    @Query("SELECT * FROM checkpoints WHERE raceId =:raceId AND distanceId =:distanceId")
    fun getCheckpointsByRaceAndDistanceId(raceId: Long, distanceId: Long): List<CheckpointTable>

    @Query("SELECT * FROM checkpoints WHERE checkpointId =:checkpointId AND distanceId =:distanceId")
    fun getCheckpoint(distanceId: Long, checkpointId: Long): CheckpointTable?

    @Query("DELETE FROM checkpoints WHERE checkpointId =:checkpointId")
    fun deleteCheckpointById(checkpointId: Long)

    @Query("DELETE FROM checkpoints WHERE raceId =:raceId AND distanceId =:distanceId")
    fun deleteCheckpointsByRaceAndDistanceId(raceId: Long, distanceId: Long)

    @Query("DELETE FROM checkpoints")
    fun deleteCheckpoints()

}


