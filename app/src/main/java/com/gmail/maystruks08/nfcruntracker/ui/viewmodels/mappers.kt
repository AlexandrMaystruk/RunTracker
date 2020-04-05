package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.CheckpointState
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
import com.gmail.maystruks08.nfcruntracker.core.ext.toTimeUTCFormat

fun Runner.toRunnerView() = RunnerView(
    this.id,
    this.number.toString(),
    this.fullName,
    this.city,
    this.totalResult?.toTimeUTCFormat(),
    this.dateOfBirthday.toDateFormat(),
    this.type == RunnerType.IRON,
    this.checkpoints.indexOfFirst { it.state  == CheckpointState.CURRENT }
)

fun Runner.toRunnerResultView(position: Int) = RunnerResultView(
    this.id,
    this.fullName,
    this.number.toString(),
    this.totalResult!!.toTimeUTCFormat(),
    position)
