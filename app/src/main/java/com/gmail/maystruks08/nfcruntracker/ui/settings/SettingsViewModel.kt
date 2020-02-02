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

    init {
        viewModelScope.launch {
            val config = repository.getInitialConfig()
            configLiveData.postValue(config)
        }
    }

    fun onCurrentCheckpointChangedForRunners(checkpointNumber: Int) {
        viewModelScope.launch {
            repository.changeCurrentCheckpointForRunners(checkpointNumber)
            toastLiveData.postValue("Settings changed")
        }
    }

    fun onCurrentCheckpointChangedForIronPeoples(checkpointNumber: Int) {
        viewModelScope.launch {
            repository.changeCurrentCheckpointForIronPeoples(checkpointNumber)
            toastLiveData.postValue("Settings changed")
        }
    }

    fun onBackClicked() {
        router.exit()
    }
}