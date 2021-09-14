package com.gmail.maystruks08.nfcruntracker.ui.race

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.RaceInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.view_models.RaceView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.toView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class RaceViewModel @ViewModelInject constructor(
    private val router: Router,
    private val interactor: RaceInteractor
) : BaseViewModel() {

    val races: LiveData<List<RaceView>> get() = _racesLiveData.map { it.toList() }
    val showProgress: SingleLiveEvent<Boolean> get() = _showProgressLiveData
    val showCreateRaceDialog: SingleLiveEvent<Boolean> get() = _showCreateRaceDialogLiveData

    private val _racesLiveData = MutableLiveData<MutableList<RaceView>>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showCreateRaceDialogLiveData = SingleLiveEvent<Boolean>()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.subscribeToUpdates()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun initUI() {
        _showProgressLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .getRaceList()
                .catch { error ->
                    handleError(error)
                }
                .collect {
                    val raceViews = it.map { it.toView() }.toMutableList()
                    _racesLiveData.postValue(raceViews)
                    _showProgressLiveData.postValue(false)
                }
        }
    }

    fun onCreateNewRaceClicked() {
        _showCreateRaceDialogLiveData.postValue(true)
    }

    fun onRaceClicked(raceView: RaceView) {
        viewModelScope.launch {
            when (interactor.saveLastSelectedRace(raceView.id, raceView.name)) {
                is TaskResult.Value -> router.navigateTo(
                    Screens.MainScreen(
                        raceView.id,
                        raceView.name,
                        raceView.firstDistanceId
                    )
                )
                is TaskResult.Error -> router.navigateTo(
                    Screens.MainScreen(
                        raceView.id,
                        raceView.name,
                        raceView.firstDistanceId
                    )
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                when (val result = interactor.getRaceList(query)) {
                    is TaskResult.Value -> {
                        val races = result.value
                        val raceViews = races.map { it.toView() }.toMutableList()
                        _racesLiveData.postValue(raceViews)
                    }
                    is TaskResult.Error -> handleError(result.error)
                }
            } else initUI()
        }
    }

    private fun handleError(throwable: Throwable) {
        _showProgressLiveData.postValue(false)
        Timber.e(throwable)
    }
}