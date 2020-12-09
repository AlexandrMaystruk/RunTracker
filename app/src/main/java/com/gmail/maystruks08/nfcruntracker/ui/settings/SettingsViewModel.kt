package com.gmail.maystruks08.nfcruntracker.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*

@ObsoleteCoroutinesApi
class SettingsViewModel @ViewModelInject constructor(
    private val router: Router,
    private val repository: SettingsRepository,
    private val startRunTrackerBus: StartRunTrackerBus,
    private val logOutUseCase: LogOutUseCase
) : BaseViewModel() {

    val config get(): LiveData<SettingsRepository.CheckpointsConfig> = configLiveData
    val start get(): LiveData<Date> = startCommandLiveData
    val changeStartButtonVisibility get(): LiveData<Boolean> = changeStartButtonVisibilityLiveData

    private val configLiveData = SingleLiveEvent<SettingsRepository.CheckpointsConfig>()
    private val startCommandLiveData = SingleLiveEvent<Date>()
    private val changeStartButtonVisibilityLiveData = SingleLiveEvent<Boolean>()


    private var isFirstStart = true

    init {
        getCachedConfig()
        updateConfig()
    }

    private fun updateConfig(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = repository.updateConfig()) {
                is TaskResult.Value -> getCachedConfig()
                is TaskResult.Error -> Timber.e(resultOfTask.error)
            }
        }
    }

    private fun getCachedConfig(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = repository.getCachedConfig()) {
                is TaskResult.Value -> {
                    configLiveData.postValue(resultOfTask.value)
                    resolveStartButtonVisibility()
                }
                is TaskResult.Error -> Timber.e(resultOfTask.error)
            }
        }
    }

    fun onInitViewsStarted() {
        isFirstStart = true
    }

    fun onCurrentCheckpointChangedForRunners(checkpointNumber: Int) {
        if (!isFirstStart) {
            viewModelScope.launch { repository.changeCurrentCheckpoint(checkpointNumber) }
        }
    }

    fun onCurrentCheckpointChangedForIronPeoples(checkpointNumber: Int) {
        if (!isFirstStart) {
            viewModelScope.launch { repository.changeCurrentCheckpointForIronPeoples(checkpointNumber) }
        }
        isFirstStart = false
    }

    fun onCompetitionStart(dateOfStart: Date) {
        if (!isFirstStart) {
            viewModelScope.launch {
                repository.changeStartDate(dateOfStart)
                startRunTrackerBus.onStartCommand(dateOfStart)
                startCommandLiveData.value = dateOfStart
                toastLiveData.postValue("Competition started!")
            }
        }
        isFirstStart = false
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            val isSuccess = logOutUseCase.logout()
            if (isSuccess) {
                router.newRootScreen(Screens.LoginScreen())
            } else {
                toastLiveData.postValue("Logout error")
            }
        }
    }

    private fun resolveStartButtonVisibility(){
        val isCurrentUserAdmin = repository.getAdminUserIds().contains(FirebaseAuth.getInstance().currentUser?.uid)
        changeStartButtonVisibilityLiveData.postValue(isCurrentUserAdmin)
    }

    fun onBackClicked() {
        router.exit()
    }
}