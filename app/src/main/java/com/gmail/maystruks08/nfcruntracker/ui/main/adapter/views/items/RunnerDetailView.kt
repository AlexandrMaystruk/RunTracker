package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items

import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView

data class RunnerDetailView(
    val number: String,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val actualDistanceId: String,
    val progress: List<CheckpointView>,
    val cardId: String?,
    val isOffTrack: Boolean
)