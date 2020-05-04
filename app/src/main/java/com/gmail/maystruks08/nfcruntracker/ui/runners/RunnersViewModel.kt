package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.launch
import javax.inject.Inject

class RunnersViewModel @Inject constructor(private val runnersInteractor: RunnersInteractor) : BaseViewModel() {

    val runners get() = _runnersLiveData
    val runnerAdd get() = _runnerAddLiveData
    val runnerUpdate get() = _runnerUpdateLiveData
    val runnerRemove get() = _runnerRemoveLiveData

    private val _runnersLiveData = MutableLiveData<MutableList<RunnerView>>()
    private val _runnerAddLiveData = MutableLiveData<RunnerView>()
    private val _runnerUpdateLiveData = MutableLiveData<RunnerView>()
    private val _runnerRemoveLiveData = MutableLiveData<RunnerView>()

    private lateinit var runnerType: RunnerType
    private lateinit var checkpointsTitles: Array<String>

    fun initFragment(runnerTypeId: Int, titles: Array<String>) {
        checkpointsTitles = titles
        runnerType = RunnerType.fromOrdinal(runnerTypeId)
        viewModelScope.launch {
            showAllRunners()
            runnersInteractor.updateRunnersCache(runnerType, ::onRunnersUpdates)
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(cardId, runnerType)) {
                is ResultOfTask.Value -> handleRunnerChanges(onResult.value)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private suspend fun showAllRunners() {
        when (val result = runnersInteractor.getAllRunners(runnerType)) {
            is ResultOfTask.Value -> _runnersLiveData.value = result.value.map { it.toRunnerView(checkpointsTitles) }.toMutableList()
            is ResultOfTask.Error -> handleError(result.error)
        }
    }

    private fun onRunnersUpdates(onResult: ResultOfTask<Exception, RunnerChange>) {
        when (onResult) {
            is ResultOfTask.Value -> handleRunnerChanges(onResult.value)
            is ResultOfTask.Error -> handleError(onResult.error)
        }
    }

    private fun handleRunnerChanges(runnerChange: RunnerChange) {
        val runnerView = runnerChange.runner.toRunnerView(checkpointsTitles)
        when (runnerChange.changeType) {
            Change.ADD -> _runnerAddLiveData.value = runnerView
            Change.UPDATE -> _runnerUpdateLiveData.value = runnerView
            Change.REMOVE -> _runnerRemoveLiveData.value = runnerView
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                when (val result = runnersInteractor.getAllRunners(runnerType)) {
                    is ResultOfTask.Value -> {
                        val pattern = ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        _runnersLiveData.postValue(
                            result.value.filter {
                                pattern.containsMatchIn(it.number.toString().toLowerCase())
                            }.map { it.toRunnerView(checkpointsTitles) }.toMutableList()
                        )
                    }
                    is ResultOfTask.Error -> handleError(result.error)
                }
            } else {
                showAllRunners()
            }
        }
    }

    private fun handleError(e: Exception) {
        when(e){
            is SaveRunnerDataException -> toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден")
            is SyncWithServerException -> toastLiveData.postValue("Данные не сохранились на сервер")
            else -> e.printStackTrace()
        }
    }
}