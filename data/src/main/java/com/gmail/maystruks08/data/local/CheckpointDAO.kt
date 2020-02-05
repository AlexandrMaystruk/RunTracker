package com.gmail.maystruks08.data.local

import androidx.room.*

@Dao
interface CheckpointDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: RunnerTable)

    @Update
    fun update(menu: RunnerTable)



}


