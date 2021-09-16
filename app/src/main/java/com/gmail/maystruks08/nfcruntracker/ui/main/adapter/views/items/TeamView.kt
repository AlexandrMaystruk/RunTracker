package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem

data class TeamView(
    override val id: String,
    val teamName: String,
    val teamResult: String?,
    val runners: List<RunnerView>
) : RunnerScreenItem