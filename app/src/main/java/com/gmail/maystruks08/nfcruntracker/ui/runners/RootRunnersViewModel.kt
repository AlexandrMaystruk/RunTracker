package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RootRunnersViewModel @Inject constructor(
    private val router: Router,
    private val startRunTrackerBus: StartRunTrackerBus,
    private val runnersInteractor: RunnersInteractor
) : BaseViewModel() {

    init {
        startRunTrackerBus.subscribeStartCommandEvent(this.name(), ::onRunningStart)
    }

    val invalidateRunnerList get(): LiveData<Unit> = invalidateRunnerListLiveData
    private val invalidateRunnerListLiveData = MutableLiveData<Unit>()

    private fun onRunningStart(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult =  runnersInteractor.addStartCheckpointToRunners(date)) {
                is ResultOfTask.Value -> invalidateRunnerListLiveData.postValue(Unit)
                is ResultOfTask.Error -> Timber.e(onResult.error)
            }
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen())
    }

    fun onClickedAtRunner(runnerNumber: Int, runnerType: Int) {
        router.navigateTo(Screens.RunnerScreen(runnerNumber, runnerType))
    }

    fun onShowResultsClicked() {
        router.navigateTo(Screens.RunnersResultsScreen())
    }

    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        super.onCleared()
    }
}