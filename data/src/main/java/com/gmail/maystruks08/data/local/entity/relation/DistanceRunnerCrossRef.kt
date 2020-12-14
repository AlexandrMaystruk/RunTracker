package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.Entity

@Entity(primaryKeys = ["distanceId", "runnerNumber"])
data class DistanceRunnerCrossRef(val distanceId: Long, val runnerNumber: Long)