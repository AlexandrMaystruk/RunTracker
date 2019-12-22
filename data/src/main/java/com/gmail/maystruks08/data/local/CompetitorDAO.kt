package com.gmail.maystruks08.data.local

import androidx.room.*

@Dao
interface CompetitorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: CompetitorResultTable)


    @Update
    fun update(menu: CompetitorResultTable)

    @Update
    fun update(menus: List<CompetitorResultTable>)

    @Delete
    fun delete(menu: CompetitorResultTable)

    @Query("DELETE FROM users")
    fun dropTable()
}


