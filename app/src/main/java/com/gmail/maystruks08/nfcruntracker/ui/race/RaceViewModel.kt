package com.gmail.maystruks08.nfcruntracker.ui.race

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.RaceInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RaceView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@ObsoleteCoroutinesApi
class RaceViewModel @ViewModelInject constructor(
    private val router: Router,
    private val interactor: RaceInteractor
) : BaseViewModel() {

    val races: LiveData<MutableList<RaceView>> get() = _racesLiveData
    val showProgress: SingleLiveEvent<Boolean> get() = _showProgressLiveData
    val showCreateRaceDialog: SingleLiveEvent<Boolean> get() = _showCreateRaceDialogLiveData

    private val _racesLiveData = MutableLiveData<MutableList<RaceView>>()
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _showCreateRaceDialogLiveData = SingleLiveEvent<Boolean>()


    init {
        _showProgressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor
                    .subscribeToUpdates()
                    .collect { updatedRaceList ->
                        _racesLiveData.postValue(updatedRaceList.map { it.toView() }
                            .toMutableList())
                        _showProgressLiveData.postValue(false)
                    }
            } catch (e: Exception) {
                _showProgressLiveData.postValue(false)
                Timber.e(e)
            }
        }
    }


    fun initUI() {
        _showProgressLiveData.value = true
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = interactor.getRaceList()) {
                is TaskResult.Value -> {
                    val raceViews = result.value.map { it.toView() }.toMutableList()
                    _racesLiveData.postValue(raceViews)
                    _showProgressLiveData.postValue(false)
                }
                is TaskResult.Error -> handleError(result.error)
            }
        }
    }

    fun onCreateNewRaceClicked() {
        _showCreateRaceDialogLiveData.postValue(true)
    }

    fun onRaceClicked(raceView: RaceView) {
        viewModelScope.launch {
            when (interactor.saveLastSelectedRaceId(raceView.id)) {
                is TaskResult.Value -> router.navigateTo(
                    Screens.RunnersScreen(
                        raceView.id,
                        raceView.firstDistanceId
                    )
                )
                is TaskResult.Error -> router.navigateTo(
                    Screens.RunnersScreen(
                        raceView.id,
                        raceView.firstDistanceId
                    )
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                when (val result = interactor.getRaceList()) {
                    is TaskResult.Value -> {
                        val pattern = ".*${query.isolateSpecialSymbolsForRegex().toLowerCase(Locale.getDefault())}.*".toRegex()
                        val races = result.value.filter { pattern.containsMatchIn(it.name.toLowerCase(Locale.getDefault())) }
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