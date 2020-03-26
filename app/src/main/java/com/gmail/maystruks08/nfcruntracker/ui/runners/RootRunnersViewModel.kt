package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RootRunnersViewModel @Inject constructor(
    private val router: Router,
    private val context: Context
) : BaseViewModel() {

    fun onOpenSettingsFragmentClicked() {
        router.navigateTo(Screens.SettingsScreen())
    }

    fun onRegisterNewRunnerClicked(){
        router.navigateTo(Screens.RegisterNewRunnerScreen())
    }

    fun onClickedAtRunner(runnerView: RunnerView) {
        router.navigateTo(Screens.RunnerScreen(runnerView))
    }

    fun onSignOutClicked() {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                router.newRootScreen(Screens.LoginScreen())
            }
    }
}