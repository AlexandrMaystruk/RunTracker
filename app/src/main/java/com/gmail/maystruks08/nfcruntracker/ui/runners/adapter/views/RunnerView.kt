package com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.views

import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView

data class RunnerView(
    val cardId: String?,
    override val number: Long,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val actualDistanceId: String,
    val progress: List<CheckpointView>,
    val isOffTrack: Boolean,
    val placeholder: Boolean = false
) : RunnerScreenItems