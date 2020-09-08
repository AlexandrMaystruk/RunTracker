package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.*
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerViews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RunnersViewModel @Inject constructor(private val runnersInteractor: RunnersInteractor) : BaseViewModel() {

    val runners get() = _runnersLiveData
    val runnerAdd get() = _runnerAddLiveData
    val runnerUpdate get() = _runnerUpdateLiveData
    val runnerRemove get() = _runnerRemoveLiveData
    val showDialog get() = _showSuccessDialogLiveData

    private val _runnersLiveData = MutableLiveData<MutableList<RunnerView>>()
    private val _runnerAddLiveData = MutableLiveData<RunnerView>()
    private val _runnerUpdateLiveData = MutableLiveData<RunnerView>()
    private val _runnerRemoveLiveData = MutableLiveData<RunnerView>()
    private val _showSuccessDialogLiveData = MutableLiveData<Pair<Checkpoint?, Int>>()

    private lateinit var runnerType: RunnerType

    fun initFragment(runnerTypeId: Int) {
        runnerType = RunnerType.fromOrdinal(runnerTypeId)
        viewModelScope.launch(Dispatchers.IO) {
            showAllRunners()
            updateRunnerCache()
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(cardId)) {
                is ResultOfTask.Value -> onMarkRunnerOnCheckpointSuccess(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun onMarkRunnerOnCheckpointSuccess(runnerChange: RunnerChange) {
        val lastCheckpoint =
            runnerChange.runner.checkpoints.maxByOrNull { (it as? CheckpointResult)?.date?.time ?: 0 }
        _showSuccessDialogLiveData.postValue(lastCheckpoint to runnerChange.runner.number)
        handleRunnerChanges(runnerChange)
    }

    private suspend fun showAllRunners() {
        when (val result = runnersInteractor.getRunners(runnerType)) {
            is ResultOfTask.Value -> {
                val runners = result.value.toRunnerViews().sortedBy { it.result }.toMutableList()
                _runnersLiveData.postValue(runners)
            }
            is ResultOfTask.Error -> handleError(result.error)
        }
    }

    private suspend fun updateRunnerCache(){
        runnersInteractor.updateRunnersCache(runnerType, ::onRunnersUpdates)
    }

    private fun onRunnersUpdates(onResult: ResultOfTask<Exception, RunnerChange>) {
        when (onResult) {
            is ResultOfTask.Value -> handleRunnerChanges(onResult.value)
            is ResultOfTask.Error -> handleError(onResult.error)
        }
    }

    private fun handleRunnerChanges(runnerChange: RunnerChange) {
        val runnerView = runnerChange.runner.toRunnerView()
            when (runnerChange.changeType) {
                Change.ADD -> _runnerAddLiveData.postValue(runnerView)
                Change.UPDATE -> _runnerUpdateLiveData.postValue(runnerView)
                Change.REMOVE -> _runnerRemoveLiveData.postValue(runnerView)
            }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
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

    private fun handleError(e: Exception) {
        Timber.e(e)
        when(e){
            is SaveRunnerDataException -> toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден")
            is SyncWithServerException -> toastLiveData.postValue("Данные не сохранились на сервер")
        }
    }
}