package com.gmail.maystruks08.nfcruntracker.ui.view_models

import android.graphics.Paint
import com.gmail.maystruks08.domain.entities.CheckpointStatistic
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
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerDetailScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerDetailView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamDetailView
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
            val item = iterator.next()
            add(item.toRunnerView())
        }
    }
}

fun toFinisherViews(runners: List<IRunner>): MutableList<RunnerScreenItem> {
    return mutableListOf<RunnerScreenItem>().apply {
        val iterator = runners.iterator()
        var position = 0
        while (iterator.hasNext()) {
            position++
            val item = iterator.next()
            add(item.toRunnerResultView(position))
        }
    }
}

fun IRunner.toRunnerView(): RunnerScreenItem {
   return when (this) {
        is Runner -> toRunnerView()
        is Team -> toTeamView()
       else -> throw RuntimeException("Incorrect type")
   }
}

fun IRunner.toRunnerResultView(position: Int): RunnerScreenItem {
    return when (this) {
        is Runner -> toRunnerResultView(position)
        is Team -> toTeamResultView(position)
        else -> throw RuntimeException("Incorrect type")
    }
}



fun IRunner.toRunnerDetailView(): RunnerDetailScreenItem {
    return when (this) {
        is Runner -> toRunnerDetailView()
        is Team -> toTeamDetailView()
        else -> throw RuntimeException("Incorrect type")
    }
}


private fun Runner.toRunnerDetailView(): RunnerDetailView {
    val isOffTrack = actualDistanceId == offTrackDistance
    return RunnerDetailView(
        id = this.number,
        fullName = this.fullName,
        city = this.city,
        result = result?.toUITimeFormat(),
        dateOfBirthday = this.dateOfBirthday?.toDateFormat().orEmpty(),
        actualDistanceId = this.actualDistanceId,
        progress = currentCheckpoints.toCheckpointViews(isOffTrack = isOffTrack),
        cardId = this.cardId,
        isOffTrack = isOffTrack
    )
}

private fun Team.toTeamDetailView(): TeamDetailView {
    return TeamDetailView(teamName, result?.toUITimeFormat(), runners.map { it.toRunnerDetailView() })
}

private fun Runner.toRunnerView(): RunnerView {
    val isOffTrack = actualDistanceId == offTrackDistance
    return RunnerView(
        id = this.number,
        shortName = this.shortName,
        result = result?.toUITimeFormat(),
        actualDistanceId = this.actualDistanceId,
        progress = currentCheckpoints.toCheckpointViews(isOffTrack = isOffTrack),
        isOffTrack = actualDistanceId == offTrackDistance,
        placeholder = false
    )
}


private fun Runner.toRunnerResultView(position: Int): RunnerResultView {
    return RunnerResultView(
        runnerNumber = this.number,
        runnerFullName = this.fullName,
        runnerResultTime = result!!.time.timeInMillisToTimeFormat(),
        position = position
    )
}

private fun Team.toTeamView(): TeamView {
    val firstRunner = runners.first()
    val secondRunner = runners.last()
    return TeamView(
        id = firstRunner.number + secondRunner.number,
        teamName = teamName,
        runners = runners.map { it.toRunnerView() },
        teamResult = result?.toUITimeFormat()
    )
}

private fun Team.toTeamResultView(position: Int): TeamResultView {
    val firstRunner = runners.first()
    return TeamResultView(
        position = position,
        runners = runners.map { it.toRunnerView() },
        teamName = firstRunner.currentTeamName.orEmpty(),
        teamResult = result?.toUITimeFormat()
    )
}

fun List<Checkpoint>.toCheckpointViews(isOffTrack: Boolean): List<CheckpointView> {
    sortedBy { it.getPosition() }
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

fun CheckpointStatistic.toCheckpointStatisticViewView(): CheckpointStatisticView {
    return CheckpointStatisticView(
        title = checkpointName,
        runnerCountInProgress = runnerCountInProgress.toString(),
        runnerCountWhoVisitCheckpoint = runnerCountWhoVisitCheckpoint.toString()
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
            R.color.colorAccent,
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
            R.color.colorAccent,
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
