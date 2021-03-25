package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ModifierType
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.DistanceInteractor
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.toTimeFormat
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.ext.startCoroutineTimer
import com.gmail.maystruks08.nfcruntracker.core.ext.updateElement
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertType
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.BaseRunnerView
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
    private val router: Router,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val distance get() = _distanceFlow
    val runners get() = _runnersFlow
    val showSuccessDialog get() = _showSuccessDialogLiveData
    val showConfirmationDialog get() = _showAlertDialogLiveData
    val showProgress get() = _showProgressLiveData
    val showTime get() = _showTimeLiveData
    val showSelectCheckpointDialog get() = _selectCheckpointDialogLiveData
    val closeSelectCheckpointDialog get() = _closeCheckpointDialogLiveData
    val showRunnersTitle get() = _showRunnersTitleFlow


    private val _distanceFlow = MutableStateFlow<MutableList<DistanceView>>(mutableListOf())
    private val _runnersFlow = MutableStateFlow<List<BaseRunnerView>>(mutableListOf())
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, Long>>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showTimeLiveData = SingleLiveEvent<String>()
    private val _selectCheckpointDialogLiveData = SingleLiveEvent<CurrentRaceDistance>()
    private val _closeCheckpointDialogLiveData = SingleLiveEvent<String>()
    private val _showRunnersTitleFlow = MutableStateFlow("")

    private var jobShowRunner: Job? = null
    private var jobShowAllDistances: Job? = null
    private var jobShowCurrentCheckpoint: Job? = null

    private lateinit var raceId: String

    private var distanceId: String = DEF_STRING_VALUE
    private var isResultMode: Boolean = false
    private var lastSelectedRunner: RunnerView? = null

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)

        viewModelScope.startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
            _showTimeLiveData.postValue(Date().toTimeFormat())
        }
    }

    fun init(raceId: String, distanceId: String?) {
        this.raceId = raceId
        this.distanceId = distanceId ?: DEF_STRING_VALUE
    }

    fun renderUI() {
        showDistances()
        showRunners()
        showCurrentCheckpoint()
        observeRunnerChanges()
        observeDistanceChanges()
    }

    fun changeMode() {
        isResultMode = !isResultMode
        showRunners()
    }

    fun changeDistance(distanceId: String) {
        this.distanceId = distanceId
        val updatedDistance = ArrayList(_distanceFlow.value.map {
            val isSelected = it.id == distanceId
            if (isSelected) _showRunnersTitleFlow.value = it.name
            it.copy(isSelected = isSelected)
        })
        _distanceFlow.value = updatedDistance

        showRunners()
        viewModelScope.launch(Dispatchers.IO) { showCurrentCheckpoint() }
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
                is TaskResult.Value -> handleRunnerChanges(
                    Change(
                        onResult.value,
                        ModifierType.UPDATE
                    )
                )
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

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                when (val result = runnersInteractor.getRunners(distanceId, query)) {
                    is TaskResult.Value -> {
                        val runnerViews = result.value.map { it.toRunnerView() }.toMutableList()
                        _runnersFlow.value = runnerViews
                    }
                    is TaskResult.Error -> handleError(result.error)
                }
            } else {
                Timber.v("showAllRunners  onSearchQueryChanged")
                showRunners()
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
                is TaskResult.Value -> _closeCheckpointDialogLiveData.postValue(checkpointView.bean.title)
                is TaskResult.Error -> handleError(result.error)
            }
        }
    }

    fun onCurrentCheckpointTextClicked() {
        _selectCheckpointDialogLiveData.postValue(CurrentRaceDistance(raceId, distanceId))
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

    fun onClickedAtRunner(runnerNumber: Long, distanceId: String) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber, distanceId))
    }

    fun onSelectRaceClicked() {
        router.newRootScreen(Screens.RaceListScreen())
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
                is TaskResult.Value -> _closeCheckpointDialogLiveData.postValue(result.value.getName())
                is TaskResult.Error -> {
                    if (result.error is CheckpointNotFoundException) {
                        _closeCheckpointDialogLiveData.postValue("Выбрать кп")
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                runnersInteractor.observeRunnerDataFlow(raceId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun handleScannedQrCode(code: String) {
        Timber.d("QR CODE: $code")
        val runnerNumber = code.toLongOrNull() ?: -1L
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerNumber)) {
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
                    runners.updateElement(runnerView, { (it as? RunnerView)?.number == runnerView.number })
                }
                ModifierType.REMOVE -> {
                    runners.removeAll { (it as? RunnerView)?.number == runnerView.number }
                }
            }
            _runnersFlow.value = runners
        }
    }

    private suspend fun provideAllDistances() {
        _showProgressLiveData.postValue(true)
        distanceInteractor.getDistancesFlow(raceId)
            .catch { error ->
                handleError(error)
            }
            .collect { distanceList ->
                val distanceViews = if (distanceId == DEF_STRING_VALUE) {
                    distanceList.mapIndexed { index, distance ->
                        val isSelected = index == 0
                        if (isSelected) _showRunnersTitleFlow.value = distance.name
                        distance.toView(isSelected)
                    }
                } else {
                    distanceList.map { it.toView(distanceId == it.id) }
                }.toMutableList()
                _distanceFlow.value = distanceViews
                _showProgressLiveData.postValue(false)
            }
    }

    private suspend fun provideAllRunners() {
        _showProgressLiveData.postValue(true)
        runnersInteractor
            .getRunnersFlow(distanceId)
            .catch { error ->
                handleError(error)
            }
            .collect {
                Timber.w("showAllRunners")
                val runners = toRunnerViews(it)
                _runnersFlow.value = runners
            }
        _showProgressLiveData.postValue(false)
    }

    private suspend fun provideFinisherRunners() {
        _showProgressLiveData.postValue(true)
        runnersInteractor
            .getFinishersFlow(distanceId)
            .catch { error ->
                handleError(error)
            }
            .collect {
                Timber.w("provideFinisherRunners")
                val runners = toFinisherViews(it)
                _runnersFlow.value = runners
            }
        _showProgressLiveData.postValue(false)
    }

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =
                runnersInteractor.addStartCheckpointToRunners(raceId, distanceId, date)) {
                is TaskResult.Value -> showRunners()
                is TaskResult.Error -> Timber.e(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(updatedRunner: Runner) {
        val lastCheckpoint = updatedRunner
            .checkpoints[updatedRunner.actualDistanceId]
            ?.maxByOrNull { it.getResult()?.time ?: 0 }

        _showSuccessDialogLiveData.postValue(lastCheckpoint to updatedRunner.number)
        handleRunnerChanges(Change(updatedRunner, ModifierType.UPDATE))
    }

    private fun handleError(e: Throwable) {
        _showProgressLiveData.postValue(false)
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


    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        super.onCleared()
    }

}