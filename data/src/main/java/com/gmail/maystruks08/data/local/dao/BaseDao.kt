package com.gmail.maystruks08.data.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(obj: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOrReplace(obj: List<T>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(obj: List<T>)

}