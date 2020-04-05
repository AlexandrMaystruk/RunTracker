package com.gmail.maystruks08.nfcruntracker.ui.runners

import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RootRunnersViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel() {

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

}