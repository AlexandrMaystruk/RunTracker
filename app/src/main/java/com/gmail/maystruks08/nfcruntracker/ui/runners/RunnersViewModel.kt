package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.DistanceInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.domain.toTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.ext.startCoroutineTimer
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertType
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@ObsoleteCoroutinesApi
class RunnersViewModel @ViewModelInject constructor(
    private val distanceInteractor: DistanceInteractor,
    private val runnersInteractor: RunnersInteractor,
    private val checkpointInteractor: CheckpointInteractor,
    private val router: Router,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val distance get() = _distanceLiveData
    val runners get() = _runnersLiveData
    val showSuccessDialog get() = _showSuccessDialogLiveData
    val showConfirmationDialog get() = _showAlertDialogLiveData
    val showProgress get() = _showProgressLiveData
    val showTime get() = _showTimeLiveData
    val showSelectCheckpointDialog get() = _selectCheckpointDialogLiveData

    private val _distanceLiveData = MutableLiveData<MutableList<DistanceView>>()
    private val _runnersLiveData = MutableLiveData<MutableList<RunnerView>>()
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, Long>>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showTimeLiveData = SingleLiveEvent<String>()
    private val _selectCheckpointDialogLiveData = SingleLiveEvent<Array<CheckpointView>>()

    private var raceId: Long = -1
    private var distanceId: Long = -1

    private var lastSelectedRunner: RunnerView? = null

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)

        viewModelScope.startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
            _showTimeLiveData.postValue(Date().toTimeFormat())
        }
    }

    fun initFragment(raceId: Long, distanceId: Long?) {
        this.raceId = raceId
        this.distanceId = distanceId ?: 0
        viewModelScope.launch(Dispatchers.IO) { showAllDistances() }
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
    }

    fun changeDistance(distanceId: Long) {
        this.distanceId = distanceId
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(cardId)) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun onRunnerSwipedLeft(position: Int, swipedRunner: RunnerView?) {
        _showAlertDialogLiveData.value = AlertTypeConfirmOfftrack(position)
        lastSelectedRunner = swipedRunner
    }

    fun onRunnerSwipedRight(position: Int, swipedRunner: RunnerView?) {
        _showAlertDialogLiveData.value = AlertTypeMarkRunnerAtCheckpoint(position)
        lastSelectedRunner = swipedRunner
    }

    fun onRunnerOffTrack() {
        viewModelScope.launch(Dispatchers.IO) {
            val runnerNumber = lastSelectedRunner?.number ?: return@launch
            if (lastSelectedRunner?.isOffTrack == true) return@launch
            when (val onResult = runnersInteractor.markRunnerGotOffTheRoute(runnerNumber)) {
                is TaskResult.Value -> handleRunnerChanges(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun markCheckpointAsPassed() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            when (val onResult =
                runnersInteractor.addCurrentCheckpointToRunner(lastSelectedRunner?.number ?: -1)) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun handleRunnerChanges(runnerChange: RunnerChange) {
        val runnerView = runnerChange.runner.toRunnerView()
        if(distanceId == runnerChange.runner.actualDistanceId) {
            when (runnerChange.changeType) {
                Change.ADD -> {
                    _runnersLiveData.value?.add(runnerView)
                }
                Change.UPDATE -> {
                    _runnersLiveData.value?.removeAll { it.number == runnerView.number }
                    _runnersLiveData.value?.add(runnerView)
                }
                Change.REMOVE -> {
                    _runnersLiveData.value?.removeAll { it.number == runnerView.number }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                when (val result = runnersInteractor.getRunners(distanceId)) {
                    is TaskResult.Value -> {
                        val pattern =
                            ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        val runners = result.value.filter {
                            pattern.containsMatchIn(
                                it.number.toString().toLowerCase()
                            )
                        }
                        val runnerViews = runners.map { it.toRunnerView() }.toMutableList()
                        _runnersLiveData.postValue(runnerViews)
                    }
                    is TaskResult.Error -> handleError(result.error)
                }
            } else {
                Timber.w("showAllRunners  onSearchQueryChanged")
                showAllRunners()
            }
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen())
    }

    fun onClickedAtRunner(runnerNumber: Long, distanceId: Long) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber, distanceId))
    }

    fun onShowResultsClicked() {
        router.navigateTo(Screens.RunnersResultsScreen())
    }

    fun onSelectRaceClicked() {
        router.newRootScreen(Screens.RaceListScreen())
    }

    fun onCurrentCheckpointTextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val hardcodeDistanceId = 0L
            when (val result = checkpointInteractor.getCheckpoints(hardcodeDistanceId)) {
                is TaskResult.Value -> {
                    val checkpoints = result.value.map { it.toCheckpointView() }.toTypedArray()
                    _selectCheckpointDialogLiveData.postValue(checkpoints)
                }
                is TaskResult.Error -> handleError(result.error)
            }
        }
    }

    fun onNewCurrentCheckpointSelected(checkpointView: CheckpointView) {
        checkpointView.bean
        //TODO implement
    }

    private suspend fun showAllDistances(){
        _showProgressLiveData.postValue(true)
        showSmallInitRunners()
        when (val result = distanceInteractor.getDistances()) {
            is TaskResult.Value -> {
                val distanceViews = result.value.map { it.toView() }.toMutableList()
                _distanceLiveData.postValue(distanceViews)
            }
            is TaskResult.Error -> handleError(result.error)
        }
        _showProgressLiveData.postValue(false)
    }

    private suspend fun showAllRunners() {
        _showProgressLiveData.postValue(true)
        showSmallInitRunners()
        when (val result = runnersInteractor.getRunners(distanceId, null)) {
            is TaskResult.Value -> {
                Timber.w("showAllRunners")
                val runners = toRunnerViews(result.value)
                _runnersLiveData.postValue(runners)
            }
            is TaskResult.Error -> handleError(result.error)
        }
        _showProgressLiveData.postValue(false)
    }


    private suspend fun showSmallInitRunners() {
        when (val result = runnersInteractor.getRunners(distanceId, 20)) {
            is TaskResult.Value -> {
                Timber.w("showAllRunners  showSmallInitRunners")
                val runners = toRunnerViews(result.value)
                _runnersLiveData.postValue(runners)
            }
            is TaskResult.Error -> handleError(result.error)
        }
        _showProgressLiveData.postValue(false)
    }

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addStartCheckpointToRunners(date)) {
                is TaskResult.Value -> viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
                is TaskResult.Error -> Timber.e(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint = runnerChange.runner.checkpoints.maxByOrNull { it.getResult()?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerChanges(runnerChange)
    }

    private fun handleError(e: Throwable) {
        _showProgressLiveData.postValue(false)
        Timber.e(e)
        when(e){
            is SaveRunnerDataException -> toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден")
            is SyncWithServerException -> toastLiveData.postValue("Данные не сохранились на сервер")
        }
    }

    private fun isRunnerOfftrack() = lastSelectedRunner?.isOffTrack == true

    private fun isRunnerHasResult() = !lastSelectedRunner?.result.isNullOrEmpty()


    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        super.onCleared()
    }

}