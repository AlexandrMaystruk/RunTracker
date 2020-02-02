package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.*
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
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
            runnersInteractor.updateRunnersCache(::onRunnersUpdates)
        }
    }

    private fun onRunnersUpdates(onResult: ResultOfTask<Exception, List<Runner>>){
        when (onResult) {
            is ResultOfTask.Value -> runnersLiveData.postValue(onResult.value.map { it.toRunnerView() }.toMutableList())
            is ResultOfTask.Error -> handleError(onResult.error)
        }
    }

    private suspend fun showAllRunners() {
        toastLiveData.postValue("Init runners list")
        when (val result = runnersInteractor.getAllRunners()) {
            is ResultOfTask.Value -> runnersLiveData.postValue(result.value.map { it.toRunnerView() }.toMutableList())
            is ResultOfTask.Error -> handleError(result.error)
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModelScope.launch {
            runnersInteractor.addCurrentCheckpointToRunner(cardId)?.let {
                runnerUpdateLiveData.postValue(it.toRunnerView())
                toastLiveData.postValue("Сheckpoint counted, card id:$cardId")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            if (query.isNotEmpty()) {
                when (val result = runnersInteractor.getAllRunners()) {
                    is ResultOfTask.Value -> {
                        val pattern = ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        runnersLiveData.postValue(
                            result.value.filter {
                                pattern.containsMatchIn(it.number.toString().toLowerCase())
                            }.map { it.toRunnerView() }.toMutableList()
                        )
                    }
                    is ResultOfTask.Error -> handleError(result.error)
                }
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

    private fun handleError(e: Exception) {
        e.printStackTrace()
    }
}