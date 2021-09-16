package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerDetailScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView

data class RunnerDetailView(
    override val id: String,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val actualDistanceId: String,
    val progress: List<CheckpointView>,
    val cardId: String?,
    private val isOffTrack: Boolean
): RunnerDetailScreenItem {

    override fun isOffTrack(): Boolean {
      return isOffTrack
    }

    override fun isRunnerHasResult(): Boolean {
        return !result.isNullOrEmpty()
    }

}