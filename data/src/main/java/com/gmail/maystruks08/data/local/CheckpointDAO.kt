package com.gmail.maystruks08.data.local

import androidx.room.*

@Dao
interface CheckpointDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: RunnerTable)

    @Update
    fun update(menu: RunnerTable)

    @Update
    fun update(menus: List<RunnerTable>)

    @Delete
    fun delete(menu: RunnerTable)

    @Query("DELETE FROM users")
    fun dropTable()
}


