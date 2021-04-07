package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable

@Dao
interface CheckpointDAO : BaseDao<CheckpointTable> {

    @Query("SELECT * FROM checkpoints WHERE distanceId =:distanceId")
    fun getCheckpointsByDistanceId(distanceId: String): List<CheckpointTable>

    @Query("SELECT * FROM checkpoints WHERE checkpointId =:checkpointId AND distanceId =:distanceId")
    fun getCheckpoint(distanceId: String, checkpointId: String): CheckpointTable?

    @Query("SELECT COUNT(*) FROM checkpoints WHERE distanceId =:distanceId")
    fun getCheckpointCount(distanceId: String): Int

    @Query("DELETE FROM checkpoints WHERE checkpointId =:checkpointId")
    fun deleteCheckpointById(checkpointId: Long)

    @Query("DELETE FROM checkpoints WHERE distanceId =:distanceId")
    fun deleteCheckpointsByRaceAndDistanceId(distanceId: String)

    @Query("DELETE FROM checkpoints")
    fun deleteCheckpoints()

}


