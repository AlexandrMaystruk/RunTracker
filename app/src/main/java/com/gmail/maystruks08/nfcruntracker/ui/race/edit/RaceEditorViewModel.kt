package com.gmail.maystruks08.nfcruntracker.ui.race.edit

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.Distance
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.DistanceInteractor
import com.gmail.maystruks08.domain.interactors.use_cases.SaveCheckpointsUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@ExperimentalCoroutinesApi
class RaceEditorViewModel @ViewModelInject constructor(
    private val router: Router,
    private val distanceInteractor: DistanceInteractor,
    private val saveCheckpointsUseCase: SaveCheckpointsUseCase
) : BaseViewModel() {


    private var distanceId: String = DEF_STRING_VALUE

    private val _showProgressLiveData = MutableLiveData<Boolean>()
    private val _showProgressFlow = MutableStateFlow(false)
    private val _checkpointsFlow = MutableStateFlow<MutableList<EditCheckpoint>>(mutableListOf())
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

    val distances: StateFlow<List<DistanceView>> get() = _distanceFlow
    val checkpoints: StateFlow<List<EditCheckpoint>> get() = _checkpointsFlow
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

    fun onCheckpointSwipedLeft(position: Int, item: EditCheckpointView) {
        if (item.isEditMode) {
            //remove item
            val updatedCheckpoints = _checkpointsFlow.value.toMutableList().apply {
                removeAt(position)
            }
            _checkpointsFlow.value = updatedCheckpoints
            return
        }

        //change item mode to edit
        val updatedCheckpoints = _checkpointsFlow.value.toMutableList().apply {
            removeAt(position)
            add(position, item.copy(isEditMode = true))
        }
        _checkpointsFlow.value = updatedCheckpoints
    }

    fun onCheckpointChanged(position: Int, item: EditCheckpointView) {
        val updatedCheckpoints = _checkpointsFlow.value.toMutableList().apply {
            removeAt(position)
            add(position, item)
        }
        _checkpointsFlow.value = updatedCheckpoints
    }

    fun onCreateNewCheckpointClicked(position: Int) {
        val updatedCheckpoints = _checkpointsFlow.value.toMutableList().apply {
            removeAt(position)
            add(
                position,
                EditCheckpointView(
                    UUID.randomUUID().toString(),
                    "no name",
                    CheckpointPosition.Center,
                    true
                )
            )
            add(position + 1, CreateNewCheckpointView())
        }
        _checkpointsFlow.value = updatedCheckpoints
    }

    fun onSaveChangedClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val editedCheckpoints = _checkpointsFlow.value.filterIsInstance(EditCheckpointView::class.java)
            when (val result = saveCheckpointsUseCase.invoke(
                distanceId,
                editedCheckpoints.mapIndexed { index, editCheckpointView ->   editCheckpointView.toEntity(distanceId, index) }
            )) {
                is TaskResult.Error -> handleError(result.error)
                is TaskResult.Value -> withContext(Dispatchers.Main) {router.exit()}
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private suspend fun handleError(throwable: Throwable) {
        withContext(Dispatchers.Main) {
            Timber.e(throwable)
        }
    }

    private fun List<Distance>.mapDistanceList(): MutableList<DistanceView> {
        return if (distanceId == DEF_STRING_VALUE) {
            mapIndexed { index, distance ->
                val isSelected = index == 0
                if (isSelected) {
                    distanceId = distance.id
                    val checkpoints: MutableList<EditCheckpoint> =
                        distance.checkpoints.mapIndexed { checkpointIndex, checkpoint ->
                            val checkpointPosition = when (checkpointIndex) {
                                0 -> CheckpointPosition.Start
                                distance.checkpoints.lastIndex -> CheckpointPosition.End
                                else -> CheckpointPosition.Center
                            }
                            checkpoint.toCheckpointEditView(checkpointPosition)
                        }.toMutableList()

                    checkpoints.add(lastIndex - 1, CreateNewCheckpointView())
                    _checkpointsFlow.value = checkpoints
                }
                distance.toView(isSelected)
            }
        } else {
            map {
                val isSelected = distanceId == it.id
                if (isSelected) {
                    distanceId = it.id
                    val checkpoints: MutableList<EditCheckpoint> =
                        it.checkpoints.mapIndexed { index, checkpoint ->
                            val checkpointPosition = when (index) {
                                0 -> CheckpointPosition.Start
                                it.checkpoints.lastIndex -> CheckpointPosition.End
                                else -> CheckpointPosition.Center
                            }
                            checkpoint.toCheckpointEditView(checkpointPosition)
                        }.toMutableList()
                    checkpoints.add(lastIndex - 1, CreateNewCheckpointView())
                    _checkpointsFlow.value = checkpoints
                }
                it.toView(isSelected)
            }
        }.toMutableList()
    }
}