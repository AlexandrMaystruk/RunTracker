package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.tables.CheckpointTable

@Dao
interface CheckpointDAO : BaseDao<CheckpointTable> {

    @Query("SELECT * FROM checkpoints WHERE distanceId =:distanceId")
    fun getCheckpointsByDistanceId(distanceId: Long): List<CheckpointTable>

    @Query("DELETE FROM checkpoints WHERE checkpointId =:checkpointId")
    fun deleteCheckpointById(checkpointId: Long)

    @Query("DELETE FROM checkpoints WHERE distanceId =:distanceId")
    fun deleteCheckpointsByDistanceId(distanceId: Long)

    @Query("DELETE FROM checkpoints")
    fun deleteCheckpoints()

}


