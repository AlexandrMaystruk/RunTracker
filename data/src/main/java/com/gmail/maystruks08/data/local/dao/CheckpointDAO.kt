package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.gmail.maystruks08.data.local.entity.CheckpointTable

@Dao
interface CheckpointDAO : BaseDao<CheckpointTable> {

    @Query("SELECT * FROM checkpoints WHERE type =:type")
    suspend fun getCheckpointsByType(type: Int): List<CheckpointTable>

}


