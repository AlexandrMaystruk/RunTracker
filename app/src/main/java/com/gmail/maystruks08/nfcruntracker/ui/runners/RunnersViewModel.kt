package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResult
import com.gmail.maystruks08.domain.entities.runner.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
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
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerViews
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ObsoleteCoroutinesApi
class RunnersViewModel @Inject constructor(
    private val runnersInteractor: RunnersInteractor,
    private val router: Router,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val distance get() = _distanceLiveData
    val runners get() = _runnersLiveData
    val showDialog get() = _showSuccessDialogLiveData
    val showConfirmationDialog get() = _showAlertDialogLiveData
    val showProgress get() = _showProgressLiveData
    val showTime get() = _showTimeLiveData
    val showSelectCheckpointDialog get() = _selectCheckpointDialogLiveData

    private val _distanceLiveData = MutableLiveData<MutableList<DistanceView>>()
    private val _runnersLiveData = MutableLiveData<MutableList<RunnerView>>()
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, Int>>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showTimeLiveData = SingleLiveEvent<String>()
    private val _selectCheckpointDialogLiveData = SingleLiveEvent<Unit>()

    private lateinit var runnerType: RunnerType
    private var lastSelectedRunner: RunnerView? = null

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)

        viewModelScope.startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
            _showTimeLiveData.postValue(Date().toTimeFormat())
        }
    }

    fun initFragment(runnerTypeId: Int) {
        runnerType = RunnerType.fromOrdinal(runnerTypeId)
        _distanceLiveData.value = RunnerType.values().map { DistanceView(it.ordinal, it.name) }.toMutableList()
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
    }

    fun changeRunnerType(runnerTypeId: Int) {
        runnerType = RunnerType.fromOrdinal(runnerTypeId)
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(cardId)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
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
                is ResultOfTask.Value -> handleRunnerChanges(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun markCheckpointAsPassed() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(lastSelectedRunner?.number?:-1)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun handleRunnerChanges(runnerChange: RunnerChange) {
        val runnerView = runnerChange.runner.toRunnerView()
        if(runnerType == runnerChange.runner.type) {
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
                when (val result = runnersInteractor.getRunners(runnerType)) {
                    is ResultOfTask.Value -> {
                        val pattern = ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        val runners = result.value.filter { pattern.containsMatchIn(it.number.toString().toLowerCase()) }
                        val runnerViews = runners.map { it.toRunnerView() }.toMutableList()
                        _runnersLiveData.postValue(runnerViews)
                    }
                    is ResultOfTask.Error -> handleError(result.error)
                }
            } else showAllRunners()
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen())
    }

    fun onClickedAtRunner(runnerNumber: Int, runnerType: Int) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber, runnerType))
    }

    fun onShowResultsClicked() {
        router.navigateTo(Screens.RunnersResultsScreen())
    }

    fun onCurrentCheckpointTextClicked() {
        _selectCheckpointDialogLiveData.call()
    }

    fun onNewCurrentCheckpointSelected(newCheckpointId: Int) {
        //TODO implement
    }

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addStartCheckpointToRunners(date)) {
                is ResultOfTask.Value -> viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
                is ResultOfTask.Error -> Timber.e(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint = runnerChange.runner.checkpoints.maxByOrNull { (it as? CheckpointResult)?.date?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerChanges(runnerChange)
    }

    private suspend fun showAllRunners() {
        _showProgressLiveData.postValue(true)
        showSmallInitRunners()
        when (val result = runnersInteractor.getRunners(runnerType, null)) {
            is ResultOfTask.Value -> {
                val runners = result.value.toRunnerViews()
                _runnersLiveData.postValue(runners)
            }
            is ResultOfTask.Error -> handleError(result.error)
        }
        _showProgressLiveData.postValue(false)

    }


    private suspend fun showSmallInitRunners() {
        when (val result = runnersInteractor.getRunners(runnerType, 20)) {
            is ResultOfTask.Value -> {
                val runners = result.value.toRunnerViews()
                _runnersLiveData.postValue(runners)
            }
            is ResultOfTask.Error -> handleError(result.error)
        }
        _showProgressLiveData.postValue(false)
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

    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        super.onCleared()
    }

    private fun isRunnerOfftrack() = lastSelectedRunner?.isOffTrack == true

    private fun isRunnerHasResult() = !lastSelectedRunner?.result.isNullOrEmpty()

}