package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.domain.toTimeUTCFormat
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.RunnerResultView
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.RunnerView
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
    this.totalResults[actualDistanceId]?.toTimeUTCFormat(),
    this.dateOfBirthday.toDateFormat(),
    this.actualDistanceId,
    this.checkpoints[actualDistanceId]?.toCheckpointViews().orEmpty(),
    this.isOffTrack[actualDistanceId]?:false
)

fun Runner.toRunnerResultView(position: Int) = RunnerResultView(
    this.number.toString(),
    this.fullName,
    this.totalResults[actualDistanceId]!!.toTimeUTCFormat(),
    position
)

fun List<Checkpoint>.toCheckpointViews(): List<CheckpointView> {
    val current = this.findLast { it.getResult() != null }
    return map {
        if (it.getResult() != null) {
            val state = if (current?.getId() == it.getId()) {
                StepState.CURRENT
            } else {
                if (it.hasPrevious()) StepState.DONE else StepState.DONE_WARNING
            }
            CheckpointView(it.getId(), Bean(it.getName(), state), it.getResult())
        } else {
            CheckpointView(it.getId(), Bean(it.getName(), StepState.UNDONE))
        }
    }
}

fun Checkpoint.toCheckpointView(selectedId: String?): CheckpointView {
    val id = getId()
    val stepState = if (id == selectedId) StepState.CURRENT else StepState.UNDONE
    return CheckpointView(id, Bean(getName(), stepState))
}


fun Race.toView(): RaceView {
    return RaceView(id, name, distanceList.firstOrNull()?.id)
}

fun Distance.toView(isSelected: Boolean = false): DistanceView {
    return DistanceView(id, name, isSelected)
}
