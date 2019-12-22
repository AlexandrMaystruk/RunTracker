package com.gmail.maystruks08.data.local

import androidx.room.TypeConverter
import java.util.*

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    @JvmStatic
    fun toTimeStamp(date: Date?): Long? = date?.let { date.time }


}