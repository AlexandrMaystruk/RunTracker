package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerDetailScreenItem

data class TeamDetailView(
    override val id: String,
    val teamResult: String?,
    val runners: List<RunnerDetailView>
): RunnerDetailScreenItem {

    override fun isOffTrack(): Boolean {
        return !runners.any { it.isOffTrack() }
    }

    override fun isRunnerHasResult(): Boolean {
        return !teamResult.isNullOrEmpty()
    }
}