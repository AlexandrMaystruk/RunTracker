package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerViewModel @Inject constructor(
    private val router: Router,
    private val runnersInteractor: RunnersInteractor
) : BaseViewModel() {

    val runner get() = runnerLiveData
    val showDialog get() = _showAlertDialogLiveData
    val showSuccessDialog get() = _showSuccessDialogLiveData
    val linkCardModeEnable get() = _linkCardModeEnableLiveData

    private val runnerLiveData = MutableLiveData<RunnerView>()
    private val _showAlertDialogLiveData = MutableLiveData<Unit>()
    private val _showSuccessDialogLiveData = MutableLiveData<Pair<Checkpoint?, Int>>()
    private val _linkCardModeEnableLiveData = MutableLiveData(false)

    fun onShowRunnerClicked(runnerNumber: Int) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.getRunner(runnerNumber)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleError(e: Exception) {
        e.printStackTrace()
        when (e) {
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден =(")
            is SaveRunnerDataException -> toastLiveData.postValue("Ошибка сохранения данных участника =(")
        }
    }

    fun onRunnerOffTrack() {
        viewModelScope.launch(Dispatchers.IO) {
            val runnerNumber = runner.value?.number ?: return@launch
            when (val onResult = runnersInteractor.markRunnerGotOffTheRoute(runnerNumber)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun markCheckpointAsPassed(runnerNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerNumber)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerNumber: Int, checkpointId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =
                runnersInteractor.removeCheckpointForRunner(runnerNumber, checkpointId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint = runnerChange.runner.checkpoints.maxByOrNull { (it as? CheckpointResult)?.date?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerData(runnerChange.runner)
    }

    private fun handleRunnerData(runner: Runner) {
        runnerLiveData.postValue(runner.toRunnerView())
    }

    fun onBackClicked() {
        router.exit()
    }

    fun btnMarkCheckpointAsPassedInManualClicked(){
        if (linkCardModeEnable.value == true) linkCardModeEnable.value = false
        else _showAlertDialogLiveData.value = Unit
    }

    fun onLinkCardToRunnerClick() {
        linkCardModeEnable.value = true
    }

    fun onNfcCardScanned(cardId: String) {
        if(linkCardModeEnable.value == true){
            runnerLiveData.value?.let {
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
}