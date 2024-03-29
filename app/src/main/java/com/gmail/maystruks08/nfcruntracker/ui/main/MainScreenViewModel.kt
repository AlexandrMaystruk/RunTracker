package com.gmail.maystruks08.nfcruntracker.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.entities.account.AssesLevel
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.use_cases.*
import com.gmail.maystruks08.domain.interactors.use_cases.runner.ManageRunnerCheckpointInteractor
import com.gmail.maystruks08.domain.interactors.use_cases.runner.OffTrackRunnerUseCase
import com.gmail.maystruks08.domain.interactors.use_cases.runner.SubscribeToRunnersUpdateUseCase
import com.gmail.maystruks08.domain.timeInMillisToTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.EventBus
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.updateElement
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamView
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertType
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainScreenViewModel @ViewModelInject constructor(
    private val provideRunnersUseCase: ProvideRunnersUseCase,
    private val provideFinishersUseCase: ProvideFinishersUseCase,
    private val provideDistanceListUseCase: ProvideDistanceListUseCase,
    private val provideCurrentSelectedCheckpointUseCase: GetCurrentSelectedCheckpointUseCase,
    private val provideCurrentRaceIdUseCase: ProvideCurrentRaceIdUseCase,

    private val saveCurrentSelectedCheckpointUseCase: SaveCurrentSelectedCheckpointUseCase,

    private val manageCheckpoints: ManageRunnerCheckpointInteractor,
    private val offTrackRunnerUseCase: OffTrackRunnerUseCase,


    private val subscribeToDistanceUpdateUseCase: SubscribeToDistanceUpdateUseCase,
    private val subscribeToRunnersUpdateUseCase: SubscribeToRunnersUpdateUseCase,

    private val calculateDistanceStatisticUseCase: CalculateDistanceStatisticUseCase,

    private val getAccountAccessLevelUseCase: GetAccountAccessLevelUseCase,

    private val eventBus: EventBus,

    private val router: Router
) : BaseViewModel() {

    private val _mainScreenModeFlow = MutableStateFlow<MainScreenMode>(MainScreenMode.RenderList(null))
    private val _runnersFlow = MutableStateFlow<List<RunnerScreenItem>>(mutableListOf())
    private val _distanceFlow = MutableStateFlow<MutableList<DistanceView>>(mutableListOf())

    private val _closeCheckpointDialogChannel = MutableStateFlow("Выбрать кп")
    private val _enableSelectCheckpointButtonFlow = MutableStateFlow(true)
    private val _showProgressFlow = MutableStateFlow(true)

    private val _selectedDistanceFlow = MutableStateFlow<Distance?>(null)

    private val _showSuccessDialogChannel = Channel<Pair<Checkpoint?, String>>(Channel.BUFFERED)
    private val _showAlertDialogChannel = Channel<AlertType>(Channel.BUFFERED)
    private val _showTimeChannel = Channel<TimerState>(Channel.BUFFERED)
    private val _selectCheckpointDialogChannel = Channel<CurrentRaceDistance>(Channel.BUFFERED)
    private val _messageChannel = Channel<String>(Channel.BUFFERED)


    val distance: StateFlow<List<DistanceView>> get() = _distanceFlow
    val runners: StateFlow<List<RunnerScreenItem>> get() = _runnersFlow
    val closeSelectCheckpointDialog: StateFlow<String> get() = _closeCheckpointDialogChannel

    val showProgress get() = _showProgressFlow
    val enableSelectCheckpointButton get() = _enableSelectCheckpointButtonFlow

    val showSuccessDialog get() = _showSuccessDialogChannel.receiveAsFlow()
    val showConfirmationDialog get() = _showAlertDialogChannel.receiveAsFlow()
    val showSelectCheckpointDialog get() = _selectCheckpointDialogChannel.receiveAsFlow()
    val showTime get() = _showTimeChannel.receiveAsFlow()
    val message get() = _messageChannel.receiveAsFlow()


    private var jobShowRunner: Job? = null
    private var jobShowAllDistances: Job? = null
    private var jobShowDistanceTimer: Job? = null
    private var jobShowCurrentCheckpoint: Job? = null

    private var jobObserveRunnerChanges: Job? = null

    private var lastSelectedRunner: RunnerView? = null

    init {
        observeDistanceChanges()
        observeRunnerChanges()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                _mainScreenModeFlow
                    .collect(::renderDistances)
            }
        } catch (e: Exception) {
            Timber.i("Error render $e")
        }
    }

    fun onReceiveReloadEvent(reloadEvent: EventBus.ReloadEvent?) {
        when (reloadEvent) {
            EventBus.ReloadEvent.DistanceWithRunners -> renderDistances(_mainScreenModeFlow.value)
            EventBus.ReloadEvent.UpdateCircleMenuState -> resolveDistanceStartTime(_selectedDistanceFlow.value?.dateOfStart)
            EventBus.ReloadEvent.Runners -> {
                _selectedDistanceFlow.value?.let { selectedDistance ->
                    resolveDistanceStartTime(selectedDistance.dateOfStart)
                    renderRunners(_mainScreenModeFlow.value, selectedDistance)
                }
            }
        }
    }

    private fun renderDistances(mode: MainScreenMode) {
        jobShowAllDistances?.cancel()
        jobShowRunner?.cancel()
        jobShowAllDistances = viewModelScope.launch(Dispatchers.IO) {
            provideDistanceListUseCase
                .invoke()
                .onStart { _showProgressFlow.value = true }
                .distinctUntilChanged()
                .map { it.mapDistanceList(mode.distanceId) }
                .collect { result ->
                    _distanceFlow.value = result.second
                    Timber.d("Distance rendered count: ${result.second.count()}")
                    val selectedDistance = result.first ?: return@collect
                    _selectedDistanceFlow.value = selectedDistance
                    _mainScreenModeFlow.value.distanceId = selectedDistance.id
                    renderRunners(mode, selectedDistance)
                    resolveDistanceStartTime(selectedDistance.dateOfStart)
                    showCurrentCheckpoint(selectedDistance.id)
                }
        }
    }

    private fun renderRunners(mode: MainScreenMode, currentDistance: Distance) {
        Timber.d("Render runners: cancel jobShowRunner start}")
        jobShowRunner?.cancel()
        Timber.d("Render runners: cancel jobShowRunner launch}")
        jobShowRunner = viewModelScope.launch(Dispatchers.IO) {
            when (mode) {
                is MainScreenMode.RenderList -> {
                    _enableSelectCheckpointButtonFlow.value = true
                    Timber.d("Render runners: provideRunnersUseCase invoke")
                    provideRunnersUseCase
                        .invoke(currentDistance, mode.query)
                        .onEach { Timber.d("Render runners: provideRunnersUseCase received list}") }
                        .map { toRunnerViews(it) }
                        .catch { error -> handleError(error) }
                }
                is MainScreenMode.RenderResult -> {
                    _enableSelectCheckpointButtonFlow.value = false
                    Timber.d("Render runners: provideFinishersUseCase invoke")
                    provideFinishersUseCase
                        .invoke(currentDistance, mode.query)
                        .onEach { Timber.d("Render runners: provideRunnersUseCase received list}") }
                        .map { toFinisherViews(it) }
                        .catch { error -> handleError(error) }
                }
            }.map {
                _runnersFlow.value = it
                Timber.i("Update runner list")
                return@map mode
            }.collect {
                _showProgressFlow.value = false
                Timber.i("Finished render $mode")
            }
        }
    }

    fun changeMode() {
        _mainScreenModeFlow.value = when (val mode = _mainScreenModeFlow.value) {
            is MainScreenMode.RenderList -> MainScreenMode.RenderResult(mode.distanceId!!)
            is MainScreenMode.RenderResult -> MainScreenMode.RenderList(mode.distanceId)
        }
    }

    fun changeDistance(distanceId: String) {
        _mainScreenModeFlow.value = when (_mainScreenModeFlow.value) {
            is MainScreenMode.RenderList -> MainScreenMode.RenderList(distanceId)
            is MainScreenMode.RenderResult -> MainScreenMode.RenderResult(distanceId)
        }
    }

    fun onOpenStatisticCLicked(distanceId: String) {
        viewModelScope.launch {
            val raceId = provideCurrentRaceIdUseCase.invoke()
            router.navigateTo(Screens.StatisticScreen(raceId, distanceId))
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedRunner = manageCheckpoints.addCurrentCheckpointByCardId(cardId)
                onMarkRunnerOnCheckpointSuccess(updatedRunner)
            } catch (e: Exception) {
                handleError(e)
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
            val runnerNumber = lastSelectedRunner?.id ?: return@launch
            if (lastSelectedRunner?.isOffTrack == true) return@launch
            try {
                val updatedRunner = offTrackRunnerUseCase.invoke(runnerNumber)
                handleRunnerChanges(Change(updatedRunner, ModifierType.UPDATE))
                eventBus.sendRunnerReloadEvent()
            } catch (e: Exception) {
                handleError(e)
            }
            lastSelectedRunner = null
        }
    }

    fun markCheckpointAsPassed() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            try {
                val runnerNumber = lastSelectedRunner?.id ?: kotlin.run { throw RunnerNotFoundException() }
                val updatedRunner = manageCheckpoints.addCurrentCheckpointByNumber(runnerNumber)
                onMarkRunnerOnCheckpointSuccess(updatedRunner)
            } catch (e: Exception) {
                handleError(e)
            }
            lastSelectedRunner = null
        }
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isEmpty()) {
            Timber.v("showAllRunners  onSearchQueryChanged")
            _mainScreenModeFlow.value = when (val mode = _mainScreenModeFlow.value) {
                is MainScreenMode.RenderList -> MainScreenMode.RenderList(mode.distanceId)
                is MainScreenMode.RenderResult -> MainScreenMode.RenderResult(
                    mode.distanceId ?: DEF_STRING_VALUE
                )
            }
            return
        }
        Timber.v("onSearchQueryChanged")
        _mainScreenModeFlow.value = when (val mode = _mainScreenModeFlow.value) {
            is MainScreenMode.RenderList -> MainScreenMode.RenderList(mode.distanceId, query)
            is MainScreenMode.RenderResult -> MainScreenMode.RenderResult(
                mode.distanceId ?: DEF_STRING_VALUE, query
            )
        }
    }

    fun onNewCurrentCheckpointSelected(checkpointView: CheckpointView) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                saveCurrentSelectedCheckpointUseCase.invoke(
                    _mainScreenModeFlow.value.distanceId!!,
                    checkpointView.id
                )
                _closeCheckpointDialogChannel.value = checkpointView.bean.title
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun onCurrentCheckpointTextClicked() {
        viewModelScope.launch {
            val raceId = provideCurrentRaceIdUseCase.invoke()
            val distanceId = _mainScreenModeFlow.value.distanceId.orEmpty()
            _selectCheckpointDialogChannel.send(CurrentRaceDistance(raceId, distanceId))
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        viewModelScope.launch {
            _selectedDistanceFlow.value?.let {
                router.navigateTo(Screens.RegisterNewRunnerScreen(it.raceId, it.id, it.type.name))
            }
        }
    }

    fun onScanQRCodeClicked() {
        router.navigateTo(Screens.ScanCodeScreen { scannedCode ->
            handleScannedQrCode(scannedCode)
            router.exit()
        })
    }

    fun onStartRaceClicked() {
        viewModelScope.launch {
            val accessLevel = getAccountAccessLevelUseCase.invoke()
            if (accessLevel == AssesLevel.Admin) {
                val currentDistance = _selectedDistanceFlow.value ?: kotlin.run {
                    _messageChannel.send("Дистанция не выбрана")
                    return@launch
                }
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        _showProgressFlow.value = true
                        stopObserveRunnerChanges()
                        manageCheckpoints.addStartCheckpoint(currentDistance)
                        observeRunnerChanges()
                        _showProgressFlow.value = false
                        eventBus.sendRunnerReloadEvent()
                    } catch (e: Exception) {
                        observeRunnerChanges()
                        handleError(e)
                    }
                }
                return@launch
            }
            _messageChannel.send("У Вас нет прав для старта дистанции")
        }
    }

    fun onClickedAtRunner(runnerNumber: String) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber))
    }

    fun onClickedAtTeam(team: TeamView) {
        router.navigateTo(Screens.RunnerScreen(team.runners.first().id))
    }

    fun onSelectRaceClicked() {
        router.newRootScreen(Screens.RaceListScreen())
    }

    fun onEditCurrentRaceClicked() {
        viewModelScope.launch {
            val accessLevel = getAccountAccessLevelUseCase.invoke()
            if (accessLevel == AssesLevel.Admin) {
                router.navigateTo(Screens.RaceEditorScreen())
                return@launch
            }
            _messageChannel.send("У Вас нет прав")
        }
    }

    private fun showCurrentCheckpoint(distanceId: String) {
        jobShowCurrentCheckpoint?.cancel()
        jobShowCurrentCheckpoint = viewModelScope.launch(Dispatchers.IO) {
            try {
                val checkpoint = provideCurrentSelectedCheckpointUseCase.invoke(distanceId)
                _closeCheckpointDialogChannel.value = checkpoint.getName()
            } catch (e: CheckpointNotFoundException) {
                _closeCheckpointDialogChannel.value = "Выбрать кп"
                handleError(e)
            }
        }
    }

    private fun observeDistanceChanges() {
        viewModelScope.launch {
            try {
                subscribeToDistanceUpdateUseCase
                    .invoke()
                    .collect {
                        Timber.d("Received distances data from remote")
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun observeRunnerChanges() {
        jobObserveRunnerChanges?.cancel()
        jobObserveRunnerChanges = viewModelScope.launch {
            try {
                subscribeToRunnersUpdateUseCase
                    .invoke()
                    .collect {
                        Timber.d("Received runners data from remote")
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun stopObserveRunnerChanges(){
        jobObserveRunnerChanges?.cancel()
        jobObserveRunnerChanges = null
    }

    private fun handleScannedQrCode(code: String) {
        Timber.d("QR CODE: $code")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedRunner = manageCheckpoints.addCurrentCheckpointByNumber(code)
                onMarkRunnerOnCheckpointSuccess(updatedRunner)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleRunnerChanges(runnerChange: Change<IRunner>) {
        val runnerView = runnerChange.entity.toRunnerView()
        if (_mainScreenModeFlow.value.distanceId == runnerChange.entity.actualDistanceId) {
            val runners = ArrayList(_runnersFlow.value)
            when (runnerChange.modifierType) {
                ModifierType.ADD, ModifierType.UPDATE -> {
                    runners.updateElement(runnerView, { it?.id == runnerView.id })
                }
                ModifierType.REMOVE -> {
                    runners.removeAll { it?.id == runnerView.id }
                }
            }
            _runnersFlow.value = runners
        }
    }

    private fun recalculateDistanceStatistic() {
        viewModelScope.launch(Dispatchers.Default) {
            calculateDistanceStatisticUseCase.invoke(_mainScreenModeFlow.value.distanceId)
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(updatedRunner: IRunner) {
        val lastCheckpoint = updatedRunner.lastAddedCheckpoint
        handleRunnerChanges(Change(updatedRunner, ModifierType.UPDATE))
        viewModelScope.launch { _showSuccessDialogChannel.send(lastCheckpoint to updatedRunner.id) }
        recalculateDistanceStatistic()
    }

    private fun List<Distance>.mapDistanceList(selectedDistanceId: String?): Pair<Distance?, MutableList<DistanceView>> {
        var selectedDistance: Distance? = null
        val result = if (selectedDistanceId == null) {
            mapIndexed { index, distance ->
                val isSelected = index == 0
                if (isSelected) selectedDistance = distance
                distance.toView(isSelected)
            }
        } else {
            map {
                val isSelected = selectedDistanceId == it.id
                if (isSelected) selectedDistance = it
                it.toView(isSelected)
            }
        }.toMutableList()
        return selectedDistance to result
    }

    private fun resolveDistanceStartTime(dateOfStart: Date?) {
        jobShowDistanceTimer?.cancel()
        if (dateOfStart == null) {
            viewModelScope.launch { _showTimeChannel.send(TimerState.HideTimer) }
            return
        }
        jobShowDistanceTimer = viewModelScope.launch(Dispatchers.IO) {
            _showTimeChannel.send(TimerState.ShowTimer)
            while (true) {
                val time = (System.currentTimeMillis() - dateOfStart.time).timeInMillisToTimeFormat()
                _showTimeChannel.send(TimerState.UpdateTimer(time))
                delay(1000)
            }
        }
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

    private fun isRunnerOfftrack() = lastSelectedRunner?.isOffTrack == true

    private fun isRunnerHasResult() = !lastSelectedRunner?.result.isNullOrEmpty()

}

sealed class TimerState {
    object ShowTimer : TimerState()
    data class UpdateTimer(val time: String) : TimerState()
    object HideTimer : TimerState()
}

sealed class MainScreenMode(var distanceId: String?) {
    class RenderList(distanceId: String?, val query: String? = null) : MainScreenMode(distanceId) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RenderList

            if (distanceId != other.distanceId) return false
            if (query != other.query) return false

            return true
        }

        override fun hashCode(): Int {
            var result = distanceId?.hashCode() ?: 0
            result = 31 * result + (query?.hashCode() ?: 0)
            return result
        }
    }

    class RenderResult(distanceId: String, val query: String? = null) : MainScreenMode(distanceId) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RenderResult

            if (distanceId != other.distanceId) return false
            if (query != other.query) return false

            return true
        }

        override fun hashCode(): Int {
            var result = distanceId?.hashCode() ?: 0
            result = 31 * result + (query?.hashCode() ?: 0)
            return result
        }
    }
}