package com.gmail.maystruks08.nfcruntracker.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val router: Router,
    private val context: Context,
    private val repository: SettingsRepository,
    private val startRunTrackerBus: StartRunTrackerBus
) : BaseViewModel() {

    val config get(): LiveData<SettingsRepository.Config> = configLiveData
    val start get(): LiveData<Date> = startCommandLiveData

    private val configLiveData = MutableLiveData<SettingsRepository.Config>()
    private val startCommandLiveData = MutableLiveData<Date>()


    private var isFirstStart = true

    init {
        viewModelScope.launch {
            val config = repository.getConfig()
            configLiveData.value = config
        }

        viewModelScope.launch {
            try {
                repository.updateConfig()?.let { newConfig ->
                    configLiveData.value = newConfig
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onInitViewsStarted() {
        isFirstStart = true
    }

    fun onCurrentCheckpointChangedForRunners(checkpointNumber: Int) {
        if (!isFirstStart) {
            viewModelScope.launch {
                repository.changeCurrentCheckpointForRunners(checkpointNumber)
                toastLiveData.postValue("Settings changed")
            }
        }
    }

    fun onCurrentCheckpointChangedForIronPeoples(checkpointNumber: Int) {
        if (!isFirstStart) {
            viewModelScope.launch {
                repository.changeCurrentCheckpointForIronPeoples(checkpointNumber)
                toastLiveData.postValue("Settings changed")
            }
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

    fun onBackClicked() {
        router.exit()
    }
}