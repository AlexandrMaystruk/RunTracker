package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBean

fun Checkpoint.toStepBean() = StepBean(
    this.id,
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
    this.name,
    this.surname,
    this.dateOfBirthday.toString(),
    this.checkpoints.map { it.toCheckpointView() }
)
