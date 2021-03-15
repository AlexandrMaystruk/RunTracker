package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
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
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
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
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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


    private val _distanceFlow = MutableStateFlow<MutableList<DistanceView>>(mutableListOf())
    private val _runnersFlow = MutableStateFlow<MutableList<RunnerView>>(mutableListOf())
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, Long>>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showTimeLiveData = SingleLiveEvent<String>()
    private val _selectCheckpointDialogLiveData = SingleLiveEvent<ArrayList<CheckpointView>>()
    private val _closeCheckpointDialogLiveData = SingleLiveEvent<String>()

    private lateinit var raceId: String
    private var distanceId: String = DEF_STRING_VALUE

    private var lastSelectedRunner: RunnerView? = null

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)

        viewModelScope.startCoroutineTimer(delayMillis = 0, repeatMillis = 1000) {
            _showTimeLiveData.postValue(Date().toTimeFormat())
        }
    }

    fun initFragment(raceId: String, distanceId: String?) {
        this.raceId = raceId
        this.distanceId = distanceId ?: DEF_STRING_VALUE
        viewModelScope.launch(Dispatchers.IO) { showAllDistances() }
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
        viewModelScope.launch(Dispatchers.IO) { showCurrentCheckpoint() }
        viewModelScope.launch(Dispatchers.IO) { observeRunnerChanges() }
        viewModelScope.launch(Dispatchers.IO) { observeDistanceChanges() }
    }

    fun changeDistance(distanceId: String) {
        this.distanceId = distanceId
        val updatedDistance = ArrayList(_distanceFlow.value.map { DistanceView(it.id, it.name, it.id == distanceId) })
        _distanceFlow.value = updatedDistance
        viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
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
                is TaskResult.Value -> handleRunnerChanges(Change(onResult.value, ModifierType.UPDATE))
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
                        val pattern =
                            ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        val runners = result.value.filter {
                            pattern.containsMatchIn(
                                it.number.toString().toLowerCase()
                            )
                        }
                        val runnerViews = runners.map { it.toRunnerView() }.toMutableList()
                        _runnersFlow.value = runnerViews
                    }
                    is TaskResult.Error -> handleError(result.error)
                }
            } else {
                Timber.v("showAllRunners  onSearchQueryChanged")
                showAllRunners()
            }
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen(raceId, distanceId))
    }

    fun onClickedAtRunner(runnerNumber: Long, distanceId: String) {
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
            when (val result = checkpointInteractor.getCheckpoints(raceId, distanceId)) {
                is TaskResult.Value -> {
                    val currentCheckpoint = (checkpointInteractor.getCurrentSelectedCheckpoint(
                        raceId,
                        distanceId
                    ) as? TaskResult.Value)?.value
                    val checkpoints = ArrayList(result.value.map { it.toCheckpointView(currentCheckpoint?.getId()) })
                    _selectCheckpointDialogLiveData.postValue(checkpoints)
                }
                is TaskResult.Error -> handleError(result.error)
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

    private suspend fun observeDistanceChanges() {
        try {
            distanceInteractor.observeDistanceDataFlow(raceId)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private suspend fun observeRunnerChanges() {
        try {
            runnersInteractor.observeRunnerDataFlow(raceId)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun handleRunnerChanges(runnerChange: Change<Runner>) {
        val runnerView = runnerChange.entity.toRunnerView()
        if (distanceId == runnerChange.entity.actualDistanceId) {
            val runners = ArrayList(_runnersFlow.value)
            when (runnerChange.modifierType) {
                ModifierType.ADD, ModifierType.UPDATE -> {
                    runners.updateElement(runnerView, { it.number == runnerView.number })
                }
                ModifierType.REMOVE -> {
                    runners.removeAll { it.number == runnerView.number }
                }
            }
            _runnersFlow.value = runners
        }
    }

    private suspend fun showAllDistances() {
        _showProgressLiveData.postValue(true)
        distanceInteractor.getDistancesFlow(raceId)
            .catch { error ->
                handleError(error)
            }
            .collect { distanceList ->
                val distanceViews = if (distanceId == DEF_STRING_VALUE) {
                    distanceList.mapIndexed { index, distance ->
                        distance.toView(index == 0)
                    }
                } else {
                    distanceList.map { it.toView() }
                }.toMutableList()
                _distanceFlow.value = distanceViews
                _showProgressLiveData.postValue(false)
            }
    }

    private suspend fun showCurrentCheckpoint() {
        when (val result = checkpointInteractor.getCurrentSelectedCheckpoint(raceId, distanceId)) {
            is TaskResult.Value -> _closeCheckpointDialogLiveData.postValue(result.value.getName())
            is TaskResult.Error -> {
                if(result.error is CheckpointNotFoundException){
                    _closeCheckpointDialogLiveData.postValue("Выбрать кп")
                }
                handleError(result.error)
            }
        }
    }

    private suspend fun showAllRunners() {
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

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addStartCheckpointToRunners(date)) {
                is TaskResult.Value -> viewModelScope.launch(Dispatchers.IO) { showAllRunners() }
                is TaskResult.Error -> Timber.e(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(updatedRunner: Runner) {
        val lastCheckpoint = updatedRunner.checkpoints[updatedRunner.actualDistanceId]?.maxByOrNull { it.getResult()?.time ?: 0 }
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