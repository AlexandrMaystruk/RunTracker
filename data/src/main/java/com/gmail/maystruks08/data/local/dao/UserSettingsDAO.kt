package com.gmail.maystruks08.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.gmail.maystruks08.data.local.entity.tables.UserSettingsTable

@Dao
interface UserSettingsDAO : BaseDao<UserSettingsTable> {

    @Query("SELECT * FROM user_settings WHERE userId =:userId AND raceId =:raceId AND distanceId =:distanceId ")
    fun getUserSettings(userId: String, raceId: Long, distanceId: Long): UserSettingsTable?

    @Query("DELETE FROM user_settings WHERE userId =:userId")
    fun deleteUserSettingById(userId: String)

    @Query("DELETE FROM user_settings")
    fun deleteAllUserSettings()

    @Update
    fun updateUserSettings(table: UserSettingsTable)

}
