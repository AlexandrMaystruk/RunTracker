package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView

data class TeamResultView(
    override val number: String,
    val teamName: String,
    val teamResult: String?,
    val runners: List<RunnerView>,
    val position: Int
) : RunnerScreenItem