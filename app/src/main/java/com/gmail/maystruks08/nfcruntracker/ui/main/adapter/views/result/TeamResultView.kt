package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView

data class TeamResultView(
    val teamName: String,
    val teamResult: String?,
    val runners: List<RunnerView>,
    val position: Int
) : RunnerScreenItem {

    override val id: String = teamName
}