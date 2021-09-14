package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result

import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem

data class TeamResultView(
    override val number: String,
    val firstRunnerNumber: String,
    val firstRunnerFullName: String,
    val firstRunnerResultTime: String,

    val secondRunnerNumber: String,
    val secondRunnerFullName: String,
    val secondRunnerResultTime: String,

    val teamName: String,
    val position: Int
) : RunnerScreenItem