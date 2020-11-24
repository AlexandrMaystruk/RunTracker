package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

sealed class AlertType(val position: Int)
class AlertTypeConfirmOfftrack(position: Int): AlertType(position)
class AlertTypeMarkRunnerAtCheckpoint(position: Int): AlertType(position)

class RunnerViewModel @Inject constructor(
    private val router: Router,
    private val runnersInteractor: RunnersInteractor
) : BaseViewModel() {

    val runner get() = _runnerLiveData
    val showDialog get() = _showAlertDialogLiveData
    val showSuccessDialog get() = _showSuccessDialogLiveData
    val linkCardModeEnable get() = _linkCardModeEnableLiveData

    private val _runnerLiveData = SingleLiveEvent<RunnerView>()
    private val _showAlertDialogLiveData = SingleLiveEvent<AlertType>()
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, Int>>()
    private val _linkCardModeEnableLiveData = SingleLiveEvent<Boolean>()

    fun onShowRunnerClicked(runnerNumber: Int) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.getRunner(runnerNumber)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
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
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun markCheckpointAsPassed(runnerNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isRunnerOfftrack() || isRunnerHasResult()) return@launch
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerNumber)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerNumber: Int, checkpointId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.removeCheckpointForRunner(runnerNumber, checkpointId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
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
                        is ResultOfTask.Value -> {
                            handleRunnerData(onResult.value.runner)
                            toastLiveData.postValue("Карта успешно изменена")
                            linkCardModeEnable.postValue(false)
                        }
                        is ResultOfTask.Error -> handleError(onResult.error)
                    }
                }
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint = runnerChange.runner.checkpoints.maxByOrNull { (it as? CheckpointResult)?.date?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerData(runnerChange.runner)
    }

    private fun handleRunnerData(runner: Runner) {
        _runnerLiveData.postValue(runner.toRunnerView())
        linkCardModeEnable.postValue(false)
    }

    private fun handleError(e: Exception) {
        e.printStackTrace()
        when (e) {
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден =(")
            is SaveRunnerDataException -> toastLiveData.postValue("Ошибка сохранения данных участника =(")
        }
    }

    private fun isRunnerOfftrack() = runner.value?.isOffTrack == true

    private fun isRunnerHasResult() = !runner.value?.result.isNullOrEmpty()
}