package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.*
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnersViewModel @Inject constructor(
    private val runnersInteractor: RunnersInteractor,
    private val router: Router
) : BaseViewModel() {

    val runners get() = runnersLiveData
    val runnerUpdate get() = runnerUpdateLiveData

    private val runnersLiveData = MutableLiveData<MutableList<RunnerView>>()
    private val runnerUpdateLiveData = MutableLiveData<RunnerView>()

    init {
        viewModelScope.launch {
            showAllRunners()
        }
    }

    private suspend fun showAllRunners() {
        toastLiveData.postValue("Start load runners")
        val allRunners = runnersInteractor.getAllRunners().toMutableList()
        runnersLiveData.postValue(allRunners.map { it.toRunnerView() }.toMutableList())
        toastLiveData.postValue("End load runners")
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch {
            toastLiveData.postValue("Start handle card = $cardId")
            runnersInteractor.addCurrentCheckpointToRunner(cardId)?.let {
                runnerUpdateLiveData.postValue(it.toRunnerView())
                toastLiveData.postValue("Сheckpoint counted")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                val allRunners = runnersInteractor.getFilteredRunners(query).toMutableList()
                runnersLiveData.postValue(allRunners.map { it.toRunnerView() }.toMutableList())
            } else {
                showAllRunners()
            }
        }
    }

    fun onRunnerClicked(runnerView: RunnerView) {
        router.navigateTo(Screens.RunnerScreen(runnerView))
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }
}