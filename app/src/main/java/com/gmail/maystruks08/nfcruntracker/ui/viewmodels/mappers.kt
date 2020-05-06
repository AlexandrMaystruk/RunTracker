package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointResult
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.domain.toTimeUTCFormat
import com.gmail.maystruks08.nfcruntracker.ui.stepview.Bean
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepState

fun Runner.toRunnerView() = RunnerView(
    this.id,
    this.number.toString(),
    this.fullName,
    this.city,
    this.totalResult?.toTimeUTCFormat(),
    this.dateOfBirthday.toDateFormat(),
    this.type.ordinal,
    this.checkpoints.toCheckpointViews()
)

fun Runner.toRunnerResultView(position: Int) = RunnerResultView(
    this.id,
    this.fullName,
    this.number.toString(),
    this.totalResult!!.toTimeUTCFormat(),
    position
)

fun List<Checkpoint>.toCheckpointViews(): List<CheckpointView> {
    val current = this.findLast { it is CheckpointResult }
    return this.map {
        if (it is CheckpointResult) {
            val state = if (current?.id == it.id) {
                StepState.CURRENT
            } else {
                if (it.hasPrevious) StepState.DONE else StepState.DONE_WARNING
            }
            CheckpointView(it.id, Bean(it.name, state), it.date)
        } else {
            CheckpointView(it.id, Bean(it.name, StepState.UNDONE))
        }
    }
}
