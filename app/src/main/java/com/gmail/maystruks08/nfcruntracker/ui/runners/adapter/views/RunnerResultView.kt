package com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.views

data class RunnerResultView(
    override val number: String,
    val runnerNumber: String,
    val runnerFullName: String,
    val runnerResultTime: String,
    val position: Int
) : RunnerScreenItems