package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerViewModel @Inject constructor(private val router: Router, private val runnersInteractor: RunnersInteractor) : BaseViewModel() {

    val runner get() = runnerLiveData

    private val runnerLiveData = MutableLiveData<Runner>()

    fun onShowRunnerClicked(runnerId: String) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.getRunner(runnerId)) {
                is ResultOfTask.Value -> runnerLiveData.postValue(onResult.value)
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
                is ResultOfTask.Value -> runnerLiveData.postValue(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerId: String, checkpointId: Int) {
        viewModelScope.launch {
            when (val onResult = runnersInteractor.removeCheckpointForRunner(runnerId, checkpointId)) {
                is ResultOfTask.Value -> runnerLiveData.postValue(onResult.value.runner)
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }
}