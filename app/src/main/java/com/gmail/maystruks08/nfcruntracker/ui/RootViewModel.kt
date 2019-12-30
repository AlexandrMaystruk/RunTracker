package com.gmail.maystruks08.nfcruntracker.ui

import androidx.lifecycle.*
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.interactors.RootInteractor
import kotlinx.coroutines.launch
import javax.inject.Inject

class RootViewModel @Inject constructor(private val rootInteractor: RootInteractor) : ViewModel() {

    val runners get() = runnersLiveData
    val toast get() = toastLiveData

    private val runnersLiveData = MutableLiveData<MutableList<Runner>>()
    private val toastLiveData = MutableLiveData<String>()

    fun showAllRunnerClicked() {
        viewModelScope.launch {
            toastLiveData.postValue("Start load runners")
            val allRunners = rootInteractor.getAllRunners().toMutableList()
            runnersLiveData.postValue(allRunners)
            toastLiveData.postValue("End load runners")
        }
    }
}