package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepState
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toCheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerViewModel @Inject constructor(private val router: Router, private val runnersInteractor: RunnersInteractor) : BaseViewModel() {

    val runnerWithCheckpoints get() = runnerWithCheckpointsLiveData

    private val runnerWithCheckpointsLiveData = MutableLiveData<Pair<RunnerView, List<CheckpointView>>>()

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
        when(e){
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден =(")
            is SaveRunnerDataException -> toastLiveData.postValue("Ошибка сохранения данных участника =(")
        }
    }

    fun markCheckpointAsPassed(runnerId: String) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.addCurrentCheckpointToRunner(runnerId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerId: String, checkpointId: Int) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.removeCheckpointForRunner(runnerId, checkpointId)) {
                is ResultOfTask.Value -> handleRunnerData(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private suspend fun handleRunnerData(runner: Runner){
        val result  = if(runner.totalResult != null){
         runner.toRunnerView() to runner.checkpoints.map { it.toCheckpointView() }
        } else {
           val maxCount =  runnersInteractor.getCheckpointCount(runner.type)
           val checkpointsView = mutableListOf<CheckpointView>()
           for (x in 0 until maxCount){
               when {
                   x < runner.checkpoints.lastIndex -> checkpointsView.add( runner.checkpoints[x].toCheckpointView())
                   x == runner.checkpoints.lastIndex -> checkpointsView.add( runner.checkpoints[x].toCheckpointView(StepState.CURRENT))
                   else -> checkpointsView.add( CheckpointView(-1, "", StepState.UNDONE))
               }
           }
           runner.toRunnerView() to checkpointsView
        }
        runnerWithCheckpointsLiveData.postValue(result)
    }

    fun onBackClicked() {
        router.exit()
    }
}