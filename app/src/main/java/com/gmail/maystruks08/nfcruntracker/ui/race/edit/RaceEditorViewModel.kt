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
import com.gmail.maystruks08.domain.interactors.use_cases.UpdateDistanceNameUseCase
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.view_models.*
import com.gmail.maystruks08.nfcruntracker.utils.ResourceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@ExperimentalCoroutinesApi
class RaceEditorViewModel @ViewModelInject constructor(
    private val router: Router,
    private val distanceInteractor: DistanceInteractor,
    private val saveCheckpointsUseCase: SaveCheckpointsUseCase,
    private val updateDistanceNameUseCase: UpdateDistanceNameUseCase,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {


    private var distanceId: String = DEF_STRING_VALUE

    private val _showProgressLiveData = MutableLiveData<Boolean>()
    private val _showProgressFlow = MutableStateFlow(false)
    private val _checkpointsFlow = MutableStateFlow<MutableList<EditCheckpoint>>(mutableListOf())
    private val _distanceFlow =
        MutableStateFlow<MutableList<EditDistanceView>>(mutableListOf()).apply {
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

    val distances: StateFlow<List<EditDistanceView>> get() = _distanceFlow
    val checkpoints: StateFlow<List<EditCheckpoint>> get() = _checkpointsFlow
    val showProgress: LiveData<Boolean> get() = _showProgressLiveData

    /**
     * Distance scope
     */
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

    fun onEditDistanceClicked(position: Int, item: EditDistanceView) {
        val updatedDistances = _distanceFlow.value.toMutableList().apply {
            removeAt(position)
            add(position, item.copy(isEditMode = true))
        }
        _distanceFlow.value = updatedDistances
    }

    fun onDistanceEdited(position: Int, item: EditDistanceView) {
        val updatedDistances = _distanceFlow.value.toMutableList().apply {
            removeAt(position)
            add(position, item.copy(isEditMode = false))
        }
        _distanceFlow.value = updatedDistances
    }

    /**
     * Checkpoint scope
     */
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
                    resourceProvider.getString(R.string.def_new_checkpoint_name),
                    CheckpointPosition.Center,
                    true
                )
            )
            add(position + 1, CreateNewCheckpointView())
        }
        _checkpointsFlow.value = updatedCheckpoints
    }

    /**
     * Common scope
     */
    fun onSaveChangedClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val updateDistanceResult = saveDistanceAsync().await()
            val updateCheckpointsResult = saveCheckpointsAsync().await()
            when {
                updateCheckpointsResult is TaskResult.Value && updateDistanceResult is TaskResult.Value -> {
                    withContext(Dispatchers.Main) { router.exit() }
                }
                updateCheckpointsResult is TaskResult.Value -> {
                    //updated only checkpoints
                }
                updateDistanceResult is TaskResult.Value -> {
                    //updated only distance name
                }
            }
        }
    }

    private suspend fun saveCheckpointsAsync(): Deferred<TaskResult<Exception, Unit>>{
        return viewModelScope.async {
            val editedCheckpoints = _checkpointsFlow.value.filterIsInstance(EditCheckpointView::class.java)
            saveCheckpointsUseCase.invoke(
                distanceId,
                editedCheckpoints.mapIndexed { index, editCheckpointView ->
                    editCheckpointView.toEntity(
                        distanceId,
                        index
                    )
                }
            )
        }
    }

    private suspend fun saveDistanceAsync():  Deferred<TaskResult<Exception, Unit>> {
        return viewModelScope.async {
            val editedDistances = _distanceFlow.value.firstOrNull { it.isSelected } ?: return@async TaskResult.build { throw RuntimeException() }
            updateDistanceNameUseCase.invoke(
                distanceId,
                editedDistances.name
            )
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

    private fun List<Distance>.mapDistanceList(): MutableList<EditDistanceView> {
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

                    checkpoints.add(checkpoints.lastIndex, CreateNewCheckpointView())
                    _checkpointsFlow.value = checkpoints
                }
                distance.toEditView(isSelected)
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
                    checkpoints.add(checkpoints.lastIndex, CreateNewCheckpointView())
                    _checkpointsFlow.value = checkpoints
                }
                it.toEditView(isSelected)
            }
        }.toMutableList()
    }
}