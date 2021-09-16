package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem

data class RunnerResultView(
    val runnerNumber: String,
    val runnerFullName: String,
    val runnerResultTime: String,
    val position: Int
) : RunnerScreenItem {
    override val id: String = runnerNumber
}