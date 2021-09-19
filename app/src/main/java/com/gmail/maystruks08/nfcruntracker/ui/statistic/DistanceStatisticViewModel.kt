package com.gmail.maystruks08.nfcruntracker.ui.statistic

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.use_cases.CalculateCheckpointStatisticUseCase
import com.gmail.maystruks08.domain.interactors.use_cases.ProvideDistanceUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

@ExperimentalCoroutinesApi
class DistanceStatisticViewModel @ViewModelInject constructor(
    private val provideDistanceUseCase: ProvideDistanceUseCase,
    private val calculateCheckpointStatisticUseCase: CalculateCheckpointStatisticUseCase,
    private val router: Router
) : BaseViewModel() {

    private val _distanceFlow = MutableStateFlow<DistanceView?>(null)
    private val _showCheckpointsStateFlow = MutableStateFlow(listOf<CheckpointStatisticView>())
    private val _showProgressFlow = MutableStateFlow(true)

    val distance: StateFlow<DistanceView?> get() = _distanceFlow
    val showCheckpoints: StateFlow<List<CheckpointStatisticView>> get() = _showCheckpointsStateFlow
    val showProgress get() = _showProgressFlow


    fun init(raceDistance: CurrentRaceDistance) {
        viewModelScope.launch {
            try {
                provideDistanceUseCase
                    .invoke(raceDistance.first, raceDistance.second)
                    .onStart { _showProgressFlow.value = true }
                    .catch { error -> handleError(error) }
                    .map { distance ->
                        val statistic = calculateCheckpointStatisticUseCase.invoke(distance.checkpoints)
                        val statisticViews = statistic.map { it.toCheckpointStatisticViewView() }
                        _showCheckpointsStateFlow.value = statisticViews
                        distance.toView()
                    }
                    .collect {
                        _showProgressFlow.value = false
                        _distanceFlow.value = it
                    }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private fun handleError(e: Throwable) {
        _showProgressFlow.value = false
        when (e) {
            is SyncWithServerException -> {
                Timber.e(e)
                toastLiveData.postValue("Данные не сохранились на сервер")
            }
            is CheckpointNotFoundException -> toastLiveData.postValue("КП не выбрано для дистанции")
            else -> Timber.e(e)
        }
    }

}