package com.gmail.maystruks08.nfcruntracker.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val router: Router,
    private val repository: SettingsRepository
) : BaseViewModel() {

    val config get() = configLiveData

    private val configLiveData = MutableLiveData<SettingsRepository.Config>()

    private var isFirstStart = true

    init {
        viewModelScope.launch {
            val config = repository.getConfig()
            configLiveData.postValue(config)
        }

        viewModelScope.launch {
            try {
                repository.updateConfig()?.let { newConfig ->
                    configLiveData.postValue(newConfig)
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun onInitViewsStarted(){
        isFirstStart = true
    }

    fun onCurrentCheckpointChangedForRunners(checkpointNumber: Int) {
        if(!isFirstStart){
            viewModelScope.launch {
                repository.changeCurrentCheckpointForRunners(checkpointNumber)
                toastLiveData.postValue("Settings changed")
            }
        }
    }

    fun onCurrentCheckpointChangedForIronPeoples(checkpointNumber: Int) {
        if(!isFirstStart) {
            viewModelScope.launch {
                repository.changeCurrentCheckpointForIronPeoples(checkpointNumber)
                toastLiveData.postValue("Settings changed")
            }
        }
        isFirstStart = false
    }

    fun onBackClicked() {
        router.exit()
    }
}