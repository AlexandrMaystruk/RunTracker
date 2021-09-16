package com.gmail.maystruks08.nfcruntracker.ui.main.dialogs

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.use_cases.GetCurrentSelectedCheckpointUseCase
import com.gmail.maystruks08.domain.interactors.use_cases.ProvideCheckpointsUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointPosition
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.toCheckpointView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class SelectCheckpointDialogViewModel @ViewModelInject constructor(
    private val provideCheckpointsUseCase: ProvideCheckpointsUseCase,
    private val provideCurrentSelectedCheckpointUseCase: GetCurrentSelectedCheckpointUseCase,
) : BaseViewModel() {

    val showCheckpoints get() = _showCheckpointsStateFlow
    val showProgress get() = _showProgressLiveData

    private val _showCheckpointsStateFlow = MutableStateFlow<ArrayList<CheckpointView>>(ArrayList())
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()


    fun init(raceDistance: CurrentRaceDistance) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val checkpoints = provideCheckpointsUseCase.invoke(raceDistance.second)
                val currentCheckpoint = try {
                    provideCurrentSelectedCheckpointUseCase.invoke(raceDistance.second)
                } catch (e: Exception) {
                    null
                }
                val checkpointViews = ArrayList(checkpoints.mapIndexed { index, it ->
                    val position = when (index) {
                        0 -> CheckpointPosition.Start
                        checkpoints.lastIndex -> CheckpointPosition.End
                        else -> CheckpointPosition.Center
                    }
                    it.toCheckpointView(position, false, currentCheckpoint?.getId())
                })
                _showCheckpointsStateFlow.value = checkpointViews
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Throwable) {
        _showProgressLiveData.postValue(false)
        when (e) {
            is SaveRunnerDataException -> {
                Timber.e(e)
                toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            }
            is RunnerNotFoundException -> {
                Timber.e(e)
                toastLiveData.postValue("Участник не найден")
            }
            is SyncWithServerException -> {
                Timber.e(e)
                toastLiveData.postValue("Данные не сохранились на сервер")
            }
            is CheckpointNotFoundException -> toastLiveData.postValue("КП не выбрано для дистанции")
            else -> Timber.e(e)
        }
    }
}