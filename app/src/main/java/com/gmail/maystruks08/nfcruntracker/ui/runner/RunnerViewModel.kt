package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.use_cases.runner.ManageRunnerCheckpointInteractor
import com.gmail.maystruks08.domain.interactors.use_cases.runner.ProvideRunnerUseCase
import com.gmail.maystruks08.nfcruntracker.core.EventBus
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.RunnerDetailScreenItem
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.toRunnerDetailView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

sealed class AlertType(val position: Int)
class AlertTypeConfirmOfftrack(position: Int): AlertType(position)
class AlertTypeMarkRunnerAtCheckpoint(position: Int): AlertType(position)

@ExperimentalCoroutinesApi
class RunnerViewModel @ViewModelInject constructor(
    private val router: Router,
    private val checkpointManager: ManageRunnerCheckpointInteractor,
    private val provideRunnerUseCase: ProvideRunnerUseCase,
    private val eventBus: EventBus
) : BaseViewModel() {

    val runner get() = _runnerLiveData
    val showSuccessDialog get() = _showSuccessDialogLiveData

    private val _runnerLiveData = SingleLiveEvent<RunnerDetailScreenItem>()
    private val _showSuccessDialogLiveData = SingleLiveEvent<Pair<Checkpoint?, String>>()

    fun onShowRunnerClicked(runnerNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val runner = provideRunnerUseCase.invoke(runnerNumber)
                handleRunnerData(runner)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun deleteCheckpointFromRunner(runnerNumber: String, checkpointId: CheckpointView) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedRunner = checkpointManager.removeCheckpoint(runnerNumber, checkpointId.id)
                handleRunnerData(updatedRunner.entity)
                eventBus.sendRunnerReloadEvent()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun onNfcCardScanned(cardId: String) {
//        if(linkCardModeEnable.value == true){
//            _runnerLiveData.value?.let {
//                viewModelScope.launch(Dispatchers.IO) {
//                    when (val onResult = runnersInteractor.changeRunnerCardId(it.number, cardId)) {
//                        is TaskResult.Value -> {
//                            handleRunnerData(onResult.value.entity)
//                            toastLiveData.postValue("Карта успешно изменена")
//                            linkCardModeEnable.postValue(false)
//                        }
//                        is TaskResult.Error -> handleError(onResult.error)
//                    }
//                }
//            }
//        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private fun handleRunnerData(runner: IRunner) {
        _runnerLiveData.postValue(runner.toRunnerDetailView())
    }

    private fun handleError(e: Exception) {
        Timber.e(e)
        when (e) {
            is RunnerNotFoundException -> toastLiveData.postValue("Участник не найден =(")
            is SaveRunnerDataException -> toastLiveData.postValue("Ошибка сохранения данных участника =(")
        }
    }
}