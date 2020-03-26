package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBean

fun Checkpoint.toStepBean() = StepBean(
    this.name,
    this.state.id
)

fun Checkpoint.toCheckpointView() = CheckpointView(
    this.id,
    this.toStepBean(),
    this.date
)

fun Runner.toRunnerView() = RunnerView(
    this.id,
    this.number.toString(),
    this.fullName,
    this.city,
    this.dateOfBirthday.toDateFormat(),
    this.checkpoints.map { it.toCheckpointView() }
)
