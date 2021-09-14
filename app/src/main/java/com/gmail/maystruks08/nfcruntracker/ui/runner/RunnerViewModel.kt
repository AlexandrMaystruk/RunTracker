package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerDetailView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.toRunnerDetailView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

sealed class AlertType(val position: Int)
class AlertTypeConfirmOfftrack(position: Int): AlertType(position)
class AlertTypeMarkRunnerAtCheckpoint(position: Int): AlertType(position)

class RunnerViewModel @ViewModelInject constructor(
    private val router: Router,
    private val runnersInteractor: RunnersInteractor
) : BaseViewModel() {

    val runner get() = _runnerLiveData
    val showDialog get() = _showAlertDialogLiveData
    val showSuccessDialog get() = _showSuccessDialogLiveData
    val linkCardModeEnable get() = _linkCardModeEnableLiveData

    private val _runnerLiveData = SingleLiveEvent<RunnerDetailView>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, String>>()
    private val _linkCardModeEnableLiveData = SingleLiveEvent<Boolean>()

    fun onShowRunnerClicked(runnerNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.getRunner(runnerNumber)) {
                is TaskResult.Value -> handleRunnerData(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun onRunnerOffTrackClicked() {
        _showAlertDialogLiveData.value = AlertTypeConfirmOfftrack(0)
    }

    fun onRunnerOffTrack() {
        viewModelScope.launch(Dispatchers.IO) {
            val runnerNumber = runner.value?.number ?: return@launch
            if (isRunnerOfftrack()) return@launch
            when (val onResult = runnersInteractor.markRunnerGotOffTheRoute(runnerNumber)) {
                is TaskResult.Value -> handleRunnerData(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun markCheckpointAsPassed(runnerNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunnerByNumber(runnerNumber)) {
                is TaskResult.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerNumber: String, checkpointId: CheckpointView) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =
                runnersInteractor.removeCheckpointForRunner(runnerNumber, checkpointId.id)) {
                is TaskResult.Value -> handleRunnerData(onResult.value.entity)
                is TaskResult.Error -> handleError(onResult.error)
            }
        }
    }

    fun btnMarkCheckpointAsPassedInManualClicked() {
        if (linkCardModeEnable.value == true) linkCardModeEnable.value = false
        else {
            if (isRunnerOfftrack() || isRunnerHasResult()) return
            _showAlertDialogLiveData.value = AlertTypeMarkRunnerAtCheckpoint(0)
        }
    }

    fun onLinkCardToRunnerClicked() {
        linkCardModeEnable.value = true
    }

    fun onNfcCardScanned(cardId: String) {
        if(linkCardModeEnable.value == true){
            _runnerLiveData.value?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    when (val onResult = runnersInteractor.changeRunnerCardId(it.number, cardId)) {
                        is TaskResult.Value -> {
                            handleRunnerData(onResult.value.entity)
                            toastLiveData.postValue("Карта успешно изменена")
                            linkCardModeEnable.postValue(false)
                        }
                        is TaskResult.Error -> handleError(onResult.error)
                    }
                }
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private fun onMarkRunnerOnCheckpointSuccess(updatedRunner: Runner) {
        val lastCheckpoint = updatedRunner.checkpoints[updatedRunner.actualDistanceId]?.maxByOrNull { it.getResult()?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to updatedRunner.number)
        handleRunnerData(updatedRunner)
    }

    private fun handleRunnerData(runner: Runner) {
        _runnerLiveData.postValue(runner.toRunnerDetailView())
        linkCardModeEnable.postValue(false)
    }

    private fun handleError(e: Exception) {
        Timber.e(e)
        when (e) {
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден =(")
            is SaveRunnerDataException -> toastLiveData.postValue("Ошибка сохранения данных участника =(")
        }
    }

    private fun isRunnerOfftrack() = runner.value?.isOffTrack == true

    private fun isRunnerHasResult() = !runner.value?.result.isNullOrEmpty()
}