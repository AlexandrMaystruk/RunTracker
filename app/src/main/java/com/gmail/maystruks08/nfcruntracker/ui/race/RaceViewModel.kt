package com.gmail.maystruks08.nfcruntracker.ui.race

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.RaceInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RaceView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toView
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

@ObsoleteCoroutinesApi
class RaceViewModel @ViewModelInject constructor(
    private val router: Router,
    private val interactor: RaceInteractor
) : BaseViewModel() {

    val races: LiveData<MutableList<RaceView>> get() = _racesLiveData
    val showProgress: LiveData<Boolean> get() = _showProgressLiveData

    private val _racesLiveData = MutableLiveData<MutableList<RaceView>>()
    private val _showProgressLiveData = MutableLiveData<Boolean>()


    fun initUI() {
        _showProgressLiveData.value = true

        viewModelScope.launch {

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


    }

    fun onRaceClicked(raceView: RaceView) {
        viewModelScope.launch {
            interactor.saveLastSelectedRaceId(raceView.id)
        }
        router.navigateTo(Screens.RunnersScreen(raceView.id, raceView.firstDistanceId))
    }

    fun onSearchQueryChanged(query: String) {
        TODO("Not yet implemented")
    }

    fun onBackClicked() {
        router.exit()
    }


    private fun handleError(throwable: Throwable) {
        _showProgressLiveData.postValue(false)
        Timber.e(throwable)
    }
}