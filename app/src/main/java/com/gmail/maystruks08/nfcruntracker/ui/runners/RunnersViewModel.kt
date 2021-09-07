package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.CalculateDistanceStatisticUseCase
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.DistanceInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.timeInMillisToTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.ext.startCoroutineTimer
import com.gmail.maystruks08.nfcruntracker.core.ext.updateElement
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertType
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.views.RunnerScreenItems
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.views.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class RunnersViewModel @ViewModelInject constructor(
    private val distanceInteractor: DistanceInteractor,
    private val runnersInteractor: RunnersInteractor,
    private val checkpointInteractor: CheckpointInteractor,
    private val calculateDistanceStatisticUseCase: CalculateDistanceStatisticUseCase,
    private val router: Router,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val distance get() = _distanceFlow.map { it.toList() }
    val runners get() = _runnersFlow
    val showSuccessDialog get() = _showSuccessDialogChannel.receiveAsFlow()
    val showConfirmationDialog get() = _showAlertDialogChannel.receiveAsFlow()
    val showSelectCheckpointDialog get() = _selectCheckpointDialogChannel.receiveAsFlow()
    val closeSelectCheckpointDialog get() = _closeCheckpointDialogChannel.receiveAsFlow()
    val showTime get() = _showTimeChannel.receiveAsFlow()

    val showProgress get() = _showProgressFlow
    val showRunnersTitle get() = _showRunnersTitleFlow
    val enableSelectCheckpointButton get() = _enableSelectCheckpointButtonFlow


    private val _distanceFlow = MutableStateFlow<MutableList<DistanceView>>(mutableListOf())
    private val _runnersFlow = MutableStateFlow<List<RunnerScreenItems>>(mutableListOf())

    private val _showSuccessDialogChannel = Channel<Pair<Checkpoint?, String>>(Channel.BUFFERED)
    private val _showAlertDialogChannel = Channel<AlertType>(Channel.BUFFERED)
    private val _showTimeChannel = Channel<String>(Channel.BUFFERED)
    private val _showProgressFlow = MutableStateFlow(true)

    private val _selectCheckpointDialogChannel = Channel<CurrentRaceDistance>(Channel.BUFFERED)
    private val _closeCheckpointDialogChannel = Channel<String>(Channel.BUFFERED)
    private val _showRunnersTitleFlow = MutableStateFlow("")
    private val _enableSelectCheckpointButtonFlow = MutableStateFlow(true)

    private var jobShowRunner: Job? = null
    private var jobShowAllDistances: Job? = null
    private var jobShowDistanceTimer: Job? = null
    private var jobShowCurrentCheckpoint: Job? = null

    private lateinit var raceId: String

    private var distanceId: String = DEF_STRING_VALUE
    private var isResultMode: Boolean = false
    private var lastSelectedRunner: RunnerView? = null

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)
    }

    fun init(raceId: String, distanceId: String?) {
        this.raceId = raceId
        this.distanceId = distanceId ?: DEF_STRING_VALUE
    }

    fun renderUI() {
        showRunners()
        showDistances()
        observeRunnerChanges()
        observeDistanceChanges()
    }

    fun changeMode() {
        isResultMode = !isResultMode
        _enableSelectCheckpointButtonFlow.value = !isResultMode
        showRunners()
    }

    fun changeDistance(distanceId: String) {
        this.distanceId = distanceId
        showRunners()
        showDistances()
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunnerByNumber(cardId)) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun onRunnerSwipedLeft(position: Int, swipedRunner: RunnerView?) {
        viewModelScope.launch {
            _showAlertDialogChannel.send(AlertTypeConfirmOfftrack(position))
        }
        lastSelectedRunner = swipedRunner
    }

    fun onRunnerSwipedRight(position: Int, swipedRunner: RunnerView?) {
        viewModelScope.launch {
            _showAlertDialogChannel.send(AlertTypeMarkRunnerAtCheckpoint(position))
        }
        lastSelectedRunner = swipedRunner
    }

    fun onRunnerOffTrack() {
        viewModelScope.launch(Dispatchers.IO) {
            val runnerNumber = lastSelectedRunner?.number ?: return@launch
            if (lastSelectedRunner?.isOffTrack == true) return@launch
            when (val onResult = runnersInteractor.markRunnerGotOffTheRoute(runnerNumber)) {
                is TaskResult.Value -> {
                    handleRunnerChanges(Change(onResult.value, ModifierType.UPDATE))
                    recalculateDistanceStatistic()
                }
                is TaskResult.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun markCheckpointAsPassed() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            when (val onResult =
                runnersInteractor.addCurrentCheckpointToRunnerByNumber(lastSelectedRunner?.number ?: "")) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
            lastSelectedRunner = null
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isEmpty()) {
                Timber.v("showAllRunners  onSearchQueryChanged")
                showRunners()
                return@launch
            }
            if (isResultMode) {
                runnersInteractor.getFinishers(distanceId, query).also {
                    when (it) {
                        is TaskResult.Value -> _runnersFlow.value = toFinisherViews(it.value)
                        is TaskResult.Error -> handleError(it.error)
                    }
                }
                return@launch
            }
            runnersInteractor.getRunners(distanceId, query).also {
                when (it) {
                    is TaskResult.Value -> _runnersFlow.value = toRunnerViews(it.value)
                    is TaskResult.Error -> handleError(it.error)
                }
            }
        }
    }

    fun onNewCurrentCheckpointSelected(checkpointView: CheckpointView) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = checkpointInteractor.saveCurrentSelectedCheckpointId(
                raceId,
                distanceId,
                checkpointView.id
            )) {
                is TaskResult.Value -> _closeCheckpointDialogChannel.send(checkpointView.bean.title)
                is TaskResult.Error -> handleError(result.error)
            }
        }
    }

    fun onCurrentCheckpointTextClicked() {
        viewModelScope.launch {
            _selectCheckpointDialogChannel.send(CurrentRaceDistance(raceId, distanceId))
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen(raceId, distanceId))
    }

    fun onScanQRCodeClicked() {
        router.navigateTo(Screens.ScanCodeScreen { scannedCode ->
            handleScannedQrCode(scannedCode)
            router.exit()
        })
    }

    fun onClickedAtRunner(runnerNumber: String, distanceId: String) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber, distanceId))
    }

    fun onSelectRaceClicked() {
        router.newRootScreen(Screens.RaceListScreen())
    }

    fun onEditCurrentRaceClicked(){
        router.navigateTo(Screens.RaceEditorScreen())
    }

    private fun showDistances() {
        jobShowAllDistances?.cancel()
        jobShowAllDistances = viewModelScope.launch(Dispatchers.IO) { provideAllDistances() }
    }

    private fun showRunners() {
        jobShowRunner?.cancel()
        jobShowRunner = when {
            isResultMode -> viewModelScope.launch(Dispatchers.IO) { provideFinisherRunners() }
            else -> viewModelScope.launch(Dispatchers.IO) { provideAllRunners() }
        }
    }

    private fun showCurrentCheckpoint() {
        jobShowCurrentCheckpoint?.cancel()
        jobShowCurrentCheckpoint = viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                checkpointInteractor.getCurrentSelectedCheckpoint(raceId, distanceId)) {
                is TaskResult.Value -> _closeCheckpointDialogChannel.send(result.value.getName())
                is TaskResult.Error -> {
                    if (result.error is CheckpointNotFoundException) {
                        _closeCheckpointDialogChannel.send("Выбрать кп")
                    }
                    handleError(result.error)
                }
            }
        }
    }

    private fun observeDistanceChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                distanceInteractor.observeDistanceDataFlow(raceId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun observeRunnerChanges() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                runnersInteractor.observeRunnerDataFlow(raceId)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun handleScannedQrCode(code: String) {
        Timber.d("QR CODE: $code")
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunnerByNumber(code)) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleRunnerChanges(runnerChange: Change<Runner>) {
        val runnerView = runnerChange.entity.toRunnerView()
        if (distanceId == runnerChange.entity.actualDistanceId) {
            val runners = ArrayList(_runnersFlow.value)
            when (runnerChange.modifierType) {
                ModifierType.ADD, ModifierType.UPDATE -> {
                    runners.updateElement(runnerView, { it?.number == runnerView.number })
                }
                ModifierType.REMOVE -> {
                    runners.removeAll { it?.number == runnerView.number }
                }
            }
            _runnersFlow.value = runners
        }
    }

    private suspend fun provideAllDistances() {
        distanceInteractor
            .getDistancesFlow(raceId)
            .onStart { _showProgressFlow.value = true }
            .catch { error ->
                handleError(error)
            }
            .collect { distanceList ->
                _showProgressFlow.value = false
                _distanceFlow.value = distanceList.mapDistanceList()
                showCurrentCheckpoint()
            }
    }

    private suspend fun provideAllRunners() {
        runnersInteractor
            .getRunnersFlow(distanceId)
            .onStart { _showProgressFlow.value = true }
            .catch { error ->
                handleError(error)
            }
            .collect {
                Timber.w("showAllRunners")
                val runners = toRunnerViews(it)
                _showProgressFlow.value = false
                _runnersFlow.value = runners
            }
    }

    private suspend fun provideFinisherRunners() {
        runnersInteractor
            .getFinishersFlow(distanceId)
            .onStart { _showProgressFlow.value = true }
            .catch { error ->
                handleError(error)
            }
            .collect {
                Timber.w("provideFinisherRunners")
                val finishers = toFinisherViews(it)
                _showProgressFlow.value = false
                _runnersFlow.value = finishers
            }
    }

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =
                runnersInteractor.addStartCheckpointToRunners(raceId, distanceId, date)) {
                is TaskResult.Value -> {
                    showRunners()
                    recalculateDistanceStatistic()
                }
                is TaskResult.Error -> Timber.e(onResult.error)
            }
        }
    }

    private fun recalculateDistanceStatistic() {
        viewModelScope.launch(Dispatchers.Default) {
            calculateDistanceStatisticUseCase.invoke(raceId, distanceId)
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(updatedRunner: Runner) {
        val lastCheckpoint = updatedRunner
            .checkpoints[updatedRunner.actualDistanceId]
            ?.maxByOrNull { it.getResult()?.time ?: 0 }

        viewModelScope.launch { _showSuccessDialogChannel.send(lastCheckpoint to updatedRunner.number) }
        handleRunnerChanges(Change(updatedRunner, ModifierType.UPDATE))
        recalculateDistanceStatistic()
    }

    private fun List<Distance>.mapDistanceList(): MutableList<DistanceView> {
        return if (distanceId == DEF_STRING_VALUE) {
            mapIndexed { index, distance ->
                val isSelected = index == 0
                if (isSelected) {
                    distanceId = distance.id
                    distance.dateOfStart?.let { showDistanceTime(it) }
                    _showRunnersTitleFlow.value = distance.name
                }
                distance.toView(isSelected)
            }
        } else {
            map {
                val isSelected = distanceId == it.id
                if (isSelected) {
                    distanceId = it.id
                    it.dateOfStart?.let { it1 -> showDistanceTime(it1) }
                    _showRunnersTitleFlow.value = it.name
                }
                it.toView(isSelected)
            }
        }.toMutableList()
    }

    private fun handleError(e: Throwable) {
        _showProgressFlow.value = false
        when (e) {
            is SaveRunnerDataException -> {
                Timber.e(e)
                toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            }
            is RunnerNotFoundException -> {
                Timber.e(e)
                toastLiveData.postValue("Участник не найден")
            }
            is SyncWithServerException -> {
                Timber.e(e)
                toastLiveData.postValue("Данные не сохранились на сервер")
            }
            is CheckpointNotFoundException -> toastLiveData.postValue("КП не выбрано для дистанции")
            else -> Timber.e(e)
        }
    }

    private fun showDistanceTime(distanceStartTime: Date) {
        jobShowDistanceTimer?.cancel()
        jobShowDistanceTimer = viewModelScope.startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
            val time = (System.currentTimeMillis() - distanceStartTime.time).timeInMillisToTimeFormat()
            viewModelScope.launch {
                _showTimeChannel.send(time)
            }
        }
    }

    private fun isRunnerOfftrack() = lastSelectedRunner?.isOffTrack == true

    private fun isRunnerHasResult() = !lastSelectedRunner?.result.isNullOrEmpty()

    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        super.onCleared()
    }

}