package com.gmail.maystruks08.data.local.entity.relation

import androidx.room.Entity

@Entity(
    tableName = "distance_runner_cross_ref",
    primaryKeys = ["distanceId", "runnerNumber"]
)
data class DistanceRunnerCrossRef(
    val distanceId: String,
    val runnerNumber: Long
)