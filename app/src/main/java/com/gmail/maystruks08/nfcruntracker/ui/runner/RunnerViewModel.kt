package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerViewModel @Inject constructor(
    private val router: Router,
    private val runnersInteractor: RunnersInteractor
) : BaseViewModel() {

    val runner get() = runnerLiveData

    private val runnerLiveData = MutableLiveData<RunnerView>()

    private lateinit var checkpointsTitles: Array<String>

    fun onShowRunnerClicked(runnerId: String, runnerType: Int, titles: Array<String>) {
        this.checkpointsTitles = titles
        viewModelScope.launch {
            when (val onResult = runnersInteractor.getRunner(runnerId, RunnerType.fromOrdinal(runnerType))) {
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

    fun markCheckpointAsPassed(runnerId: String, runnerType: Int) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerId, RunnerType.fromOrdinal(runnerType))) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerId: String, runnerType: Int,  checkpointId: Int) {
        viewModelScope.launch {
            when (val onResult =
                runnersInteractor.removeCheckpointForRunner(runnerId, checkpointId, RunnerType.fromOrdinal(runnerType))) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleRunnerData(runner: Runner) {
        runnerLiveData.postValue(runner.toRunnerView(checkpointsTitles))
    }

    fun onBackClicked() {
        router.exit()
    }
}