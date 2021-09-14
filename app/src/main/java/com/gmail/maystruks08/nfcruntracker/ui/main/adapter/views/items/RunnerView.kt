package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView

data class RunnerView(
    override val number: String,
    val shortName: String,
    val result: String?,
    val actualDistanceId: String,
    val progress: List<CheckpointView>,
    val isOffTrack: Boolean,
    val placeholder: Boolean = false
) : RunnerScreenItem