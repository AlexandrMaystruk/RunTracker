package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.domain.toTimeUTCFormat
import com.gmail.maystruks08.nfcruntracker.ui.views.Bean
import com.gmail.maystruks08.nfcruntracker.ui.views.StepState

fun toRunnerViews(runners: List<Runner>): MutableList<RunnerView> {
    return mutableListOf<RunnerView>().apply {
        val iterator = runners.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            this.add(item.toRunnerView())
        }
    }
}

fun Runner.toRunnerView() = RunnerView(
    this.cardId,
    this.number,
    this.fullName,
    this.city,
    this.totalResult?.toTimeUTCFormat(),
    this.dateOfBirthday.toDateFormat(),
    this.actualDistanceId,
    this.checkpoints.toCheckpointViews(),
    this.isOffTrack
)

fun Runner.toRunnerResultView(position: Int) = RunnerResultView(
    this.cardId,
    this.fullName,
    this.number.toString(),
    this.totalResult!!.toTimeUTCFormat(),
    position
)

fun List<Checkpoint>.toCheckpointViews(): List<CheckpointView> {
    return emptyList()
//    val current = this.findLast { it is CheckpointResultIml }
//    return this.map {
//        if (it is CheckpointResultIml) {
//            val state = if (current?.id == it.id) {
//                StepState.CURRENT
//            } else {
//                if (it.hasPrevious) StepState.DONE else StepState.DONE_WARNING
//            }
//            CheckpointView(it.id, Bean(it.name, state), it.date)
//        } else {
//            CheckpointView(it.id, Bean(it.name, StepState.UNDONE))
//        }
//    }
}

fun Checkpoint.toCheckpointView(): CheckpointView {
    return CheckpointView(getId(), Bean(getName(), StepState.UNDONE))
}


fun Race.toView(): RaceView {
    return RaceView(id, name, distanceList.firstOrNull()?.id)
}

fun Distance.toView(): DistanceView {
    return DistanceView(id, name)
}
