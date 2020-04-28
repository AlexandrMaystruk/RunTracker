package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.bus.StartRunTrackerBus
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
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

    private fun onRunningStart(date: Date) {
        viewModelScope.launch {
            runnersInteractor.addStartCheckpointToRunners(date)
        }
    }

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked() {
        router.navigateTo(Screens.RegisterNewRunnerScreen())
    }

    fun onClickedAtRunner(runnerId: String) {
        router.navigateTo(Screens.RunnerScreen(runnerId))
    }

    fun onShowResultsClicked() {
        router.navigateTo(Screens.RunnersResultsScreen())
    }

    override fun onCleared() {
        startRunTrackerBus.unsubscribe(this.name())
        viewModelScope.launch { runnersInteractor.finishWork() }
        super.onCleared()
    }
}