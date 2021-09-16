package com.gmail.maystruks08.nfcruntracker.ui.view_models

import android.graphics.Paint
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.domain.timeInMillisToTimeFormat
import com.gmail.maystruks08.domain.toDateFormat
import com.gmail.maystruks08.domain.toUITimeFormat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerDetailView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result.RunnerResultView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result.TeamResultView
import com.gmail.maystruks08.nfcruntracker.ui.views.Bean
import com.gmail.maystruks08.nfcruntracker.ui.views.ChartItem
import com.gmail.maystruks08.nfcruntracker.ui.views.StepState

fun toRunnerViews(runners: List<IRunner>): MutableList<RunnerScreenItem> {
    return mutableListOf<RunnerScreenItem>().apply {
        val iterator = runners.iterator()
        while (iterator.hasNext()) {
            when (val item = iterator.next()) {
                is Runner -> add(item.toRunnerView())
                is Team -> add(item.toTeamView())
            }
        }
    }
}

fun toFinisherViews(runners: List<IRunner>): MutableList<RunnerScreenItem> {
    return mutableListOf<RunnerScreenItem>().apply {
        val iterator = runners.iterator()
        var position = 0
        while (iterator.hasNext()) {
            position++
            when (val item = iterator.next()) {
                is Runner -> add(item.toRunnerResultView(position))
                is Team -> add(item.toTeamResultView(position))
            }
        }
    }
}

fun Runner.toRunnerView(): RunnerView {
    val isOffTrack = offTrackDistances.any { it == actualDistanceId }
    return RunnerView(
        number = this.number,
        shortName = this.shortName,
        result = this.totalResults[actualDistanceId]?.toUITimeFormat(),
        actualDistanceId = this.actualDistanceId,
        progress = this.checkpoints[actualDistanceId]?.toCheckpointViews(isOffTrack = isOffTrack).orEmpty(),
        isOffTrack = isOffTrack,
        placeholder = false
    )
}

fun Runner.toRunnerDetailView(): RunnerDetailView {
    val isOffTrack = offTrackDistances.any { it == actualDistanceId }
    return RunnerDetailView(
        number = this.number,
        fullName = this.fullName,
        city = this.city,
        result = this.totalResults[actualDistanceId]?.toUITimeFormat(),
        dateOfBirthday = this.dateOfBirthday?.toDateFormat().orEmpty(),
        actualDistanceId = this.actualDistanceId,
        progress = this.checkpoints[actualDistanceId]?.toCheckpointViews(isOffTrack = isOffTrack).orEmpty(),
        cardId = this.cardId,
        isOffTrack = isOffTrack
    )
}


fun Runner.toRunnerResultView(position: Int): RunnerResultView {
    return RunnerResultView(
        number = number,
        runnerNumber = this.number,
        runnerFullName = this.fullName,
        runnerResultTime = this.totalResults[actualDistanceId]!!.time.timeInMillisToTimeFormat(),
        position = position
    )
}

fun Team.toTeamView(): TeamView {
    val firstRunner = runners.first()
    val secondRunner = runners.last()
    return TeamView(
        number = firstRunner.number + secondRunner.number,
        teamName = teamName,
        runners = runners.map { it.toRunnerView() },
        teamResult = result
    )
}

fun Team.toTeamResultView(position: Int): TeamResultView {
    val firstRunner = runners.first()
    val secondRunner = runners.last()
    return TeamResultView(
        number = firstRunner.number + secondRunner.number,
        position = position,
        runners = runners.map { it.toRunnerView() },
        teamName = firstRunner.currentTeamName.orEmpty(),
        teamResult = result
    )
}

fun List<Checkpoint>.toCheckpointViews(isOffTrack: Boolean): List<CheckpointView> {
    val current = this.findLast { it.getResult() != null }
    val titlePaintFlag = if (isOffTrack) Paint.STRIKE_THRU_TEXT_FLAG else Paint.LINEAR_TEXT_FLAG

    return mapIndexed { index, it ->
        val title: String
        val position =
            when (index) {
                0 -> {
                    title = ""
                    CheckpointPosition.Start
                }
                lastIndex -> {
                    title = ""
                    CheckpointPosition.End
                }
                else -> {
                    title = it.getName()
                    CheckpointPosition.Center
                }
            }

        if (it.getResult() != null) {
            val state = if (current?.getId() == it.getId()) {
                StepState.CURRENT
            } else {
                if (it.hasPrevious()) StepState.DONE else StepState.DONE_WARNING
            }
            CheckpointView(
                it.getId(),
                title,
                position,
                titlePaintFlag,
                Bean(it.getName(), state),
                it.getResult()
            )
        } else {
            CheckpointView(
                it.getId(),
                title,
                position,
                titlePaintFlag,
                Bean(it.getName(), StepState.UNDONE)
            )
        }
    }
}

fun Checkpoint.toCheckpointView(
    checkpointPosition: CheckpointPosition,
    isOffTrack: Boolean,
    selectedId: String?
): CheckpointView {
    val id = getId()
    val titlePaintFlag = if (isOffTrack) Paint.STRIKE_THRU_TEXT_FLAG else Paint.LINEAR_TEXT_FLAG

    val stepState = if (id == selectedId) StepState.CURRENT else StepState.UNDONE
    return CheckpointView(
        id,
        getName(),
        checkpointPosition,
        titlePaintFlag,
        Bean(getName(), stepState)
    )
}


fun Checkpoint.toCheckpointEditView(checkpointPosition: CheckpointPosition): EditCheckpointView {
    return EditCheckpointView(
        id = getId(),
        title = getName(),
        positionState = checkpointPosition,
        isEditMode = false
    )
}


fun EditCheckpointView.toEntity(distanceId: String, position: Int): Checkpoint {
    return CheckpointImpl(
        _id = id,
        _distanceId = distanceId,
        _name = title,
        _position = position
    )
}


fun Race.toView(): RaceView {
    return RaceView(id, name, distanceList.firstOrNull()?.id)
}

fun Distance.toView(isSelected: Boolean = false): DistanceView {
    val items = arrayOf(
        ChartItem(
            statistic.runnerCountInProgress.toString(),
            R.color.colorWhite,
            R.color.design_default_color_primary,
            statistic.runnerCountInProgress
        ),
        ChartItem(
            statistic.runnerCountOffTrack.toString(),
            R.color.colorWhite,
            R.color.colorRed,
            statistic.runnerCountOffTrack
        ),
        ChartItem(
            statistic.finisherCount.toString(),
            R.color.colorWhite,
            R.color.colorGreen,
            statistic.finisherCount
        ),
    )
    return DistanceView(
        id = id,
        name = name,
        type = type.name,
        chartItems = items,
        isSelected = isSelected
    )
}

fun Distance.toEditView(isSelected: Boolean = false): EditDistanceView {
    val items = arrayOf(
        ChartItem(
            statistic.runnerCountInProgress.toString(),
            R.color.colorWhite,
            R.color.design_default_color_primary,
            statistic.runnerCountInProgress
        ),
        ChartItem(
            statistic.runnerCountOffTrack.toString(),
            R.color.colorWhite,
            R.color.colorRed,
            statistic.runnerCountOffTrack
        ),
        ChartItem(
            statistic.finisherCount.toString(),
            R.color.colorWhite,
            R.color.colorGreen,
            statistic.finisherCount
        ),
    )
    return EditDistanceView(
        id = id,
        name = name,
        chartItems = items,
        isSelected = isSelected,
        isEditMode = false
    )
}
