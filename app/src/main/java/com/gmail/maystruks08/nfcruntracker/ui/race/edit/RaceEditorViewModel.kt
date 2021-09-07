package com.gmail.maystruks08.nfcruntracker.ui.race.edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.domain.interactors.DistanceInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen
import timber.log.Timber

@ExperimentalCoroutinesApi
class RaceEditorViewModel @ViewModelInject constructor(
    private val router: Router,
    private val distanceInteractor: DistanceInteractor,
    private val checkpointInteractor: CheckpointInteractor
) : BaseViewModel() {


    private var distanceId: String = DEF_STRING_VALUE

    private val _showProgressLiveData = MutableLiveData<Boolean>()
    private val _showProgressFlow = MutableStateFlow(false)
    private val _distanceFlow = MutableStateFlow<MutableList<DistanceView>>(mutableListOf()).apply {
        viewModelScope.launch {
            val raceId = distanceInteractor.provideCurrentSelectedRaceId()
            distanceInteractor
                .getDistancesFlow(raceId)
                .onStart { _showProgressFlow.value = true }
                .catch { error ->
                    handleError(error)
                }
                .map { it.mapDistanceList() }
                .collect { distanceList ->
                    _showProgressFlow.value = false
                    value = distanceList
                }
        }
    }
    private val _checkpointsFlow = MutableStateFlow<MutableList<EditCheckpointView>>(mutableListOf())


    val distances: StateFlow<List<DistanceView>> get() = _distanceFlow
    val checkpoints: StateFlow<List<EditCheckpointView>> get() = _checkpointsFlow
    val showProgress: LiveData<Boolean> get() = _showProgressLiveData

    fun changeDistance(id: String) {
        this.distanceId = id
        viewModelScope.launch {
            val raceId = distanceInteractor.provideCurrentSelectedRaceId()
            distanceInteractor
                .getDistancesFlow(raceId)
                .onStart { _showProgressFlow.value = true }
                .catch { error ->
                    handleError(error)
                }
                .map { it.mapDistanceList() }
                .collect { distanceList ->
                    _showProgressFlow.value = false
                    _distanceFlow.value = distanceList
                }
        }
    }

    fun onSaveChangedClicked() {
        //TODO write save code
        router.exit()
    }

    fun onBackClicked(){
        router.exit()
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }

    private fun List<Distance>.mapDistanceList(): MutableList<DistanceView> {
        return if (distanceId == DEF_STRING_VALUE) {
            mapIndexed { index, distance ->
                val isSelected = index == 0
                if (isSelected) {
                    distanceId = distance.id
                    _checkpointsFlow.value = distance.checkpoints.mapIndexed { checkpointIndex, checkpoint ->
                        val checkpointPosition = when(checkpointIndex){
                            0 -> CheckpointPosition.Start
                            distance.checkpoints.lastIndex -> CheckpointPosition.End
                            else -> CheckpointPosition.Center
                        }
                        checkpoint.toCheckpointEditView(checkpointPosition)
                    }.toMutableList()
                }
                distance.toView(isSelected)
            }
        } else {
            map {
                val isSelected = distanceId == it.id
                if (isSelected) {
                    distanceId = it.id
                    _checkpointsFlow.value = it.checkpoints.mapIndexed { index, checkpoint ->
                        val checkpointPosition = when(index){
                            0 -> CheckpointPosition.Start
                            it.checkpoints.lastIndex -> CheckpointPosition.End
                            else -> CheckpointPosition.Center

                        }
                        checkpoint.toCheckpointEditView(checkpointPosition)
                    }.toMutableList()
                }
                it.toView(isSelected)
            }
        }.toMutableList()
    }
}