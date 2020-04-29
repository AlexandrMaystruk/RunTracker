package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.CheckpointResult
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.toTimeUTCFormat
import com.gmail.maystruks08.nfcruntracker.ui.stepview.Bean
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepState

fun Runner.toRunnerView(checkpointNames: Array<String>) = RunnerView(
    this.id,
    this.number.toString(),
    this.fullName,
    this.city,
    this.totalResult?.toTimeUTCFormat(),
    this.dateOfBirthday.toDateFormat(),
    this.type.ordinal,
    this.checkpoints.toCheckpointViews(checkpointNames)
)

fun Runner.toRunnerResultView(position: Int) = RunnerResultView(
    this.id,
    this.fullName,
    this.number.toString(),
    this.totalResult!!.toTimeUTCFormat(),
    position
)

fun List<CheckpointResult>.toCheckpointViews(checkpointNames: Array<String>): List<CheckpointView> {
    val checkpoints = this
    return mutableListOf<CheckpointView>().apply {
        val current = checkpoints.lastOrNull()
        for (i in 0..checkpointNames.lastIndex) {
            val checkpoint = checkpoints.find { it.id == i }
            val state = if (checkpoint != null) {
                if (current == checkpoint) {
                    StepState.CURRENT
                } else {
                    if (checkpoint.hasPrevious) StepState.DONE else StepState.DONE_WARNING
                }
            } else StepState.UNDONE
            this.add(CheckpointView(i, Bean(checkpointNames[i], state), checkpoint?.date))
        }
    }
}
