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
    val showDialog get() = _showSuccessDialogLiveData

    private val runnerLiveData = MutableLiveData<RunnerView>()
    private val _showSuccessDialogLiveData = MutableLiveData<Pair<Checkpoint?, Int>>()

    fun onShowRunnerClicked(runnerId: String) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.getRunner(runnerId)) {
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
            val runnerId = runner.value?.id ?: return@launch
            when (val onResult = runnersInteractor.markRunnerGotOffTheRoute(runnerId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun markCheckpointAsPassed(runnerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerId)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerId: String, checkpointId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =
                runnersInteractor.removeCheckpointForRunner(runnerId, checkpointId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint = runnerChange.runner.checkpoints.maxBy { (it as? CheckpointResult)?.date?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerData(runnerChange.runner)
    }

    private fun handleRunnerData(runner: Runner) {
        runnerLiveData.postValue(runner.toRunnerView())
    }

    fun onBackClicked() {
        router.exit()
    }
}