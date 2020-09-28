package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val router: Router,
    private val context: Context,
    private val repository: SettingsRepository,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val config get(): LiveData<SettingsRepository.CheckpointsConfig> = configLiveData
    val start get(): LiveData<Date> = startCommandLiveData
    val changeStartButtonVisibility get(): LiveData<Boolean> = changeStartButtonVisibilityLiveData

    private val configLiveData = MutableLiveData<SettingsRepository.CheckpointsConfig>()
    private val startCommandLiveData = MutableLiveData<Date>()
    private val changeStartButtonVisibilityLiveData = MutableLiveData<Boolean>()


    private var isFirstStart = true

    init {
        getCachedConfig()
        updateConfig()
    }

    private fun updateConfig(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = repository.updateConfig()) {
                is ResultOfTask.Value -> getCachedConfig()
                is ResultOfTask.Error -> Timber.e(resultOfTask.error)
            }
        }
    }

    private fun getCachedConfig(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = repository.getCachedConfig()) {
                is ResultOfTask.Value -> {
                    configLiveData.postValue(resultOfTask.value)
                    resolveStartButtonVisibility()
                }
                is ResultOfTask.Error -> Timber.e(resultOfTask.error)
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
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                router.newRootScreen(Screens.LoginScreen())
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