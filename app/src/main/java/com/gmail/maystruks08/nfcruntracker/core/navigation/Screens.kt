package com.gmail.maystruks08.nfcruntracker.core.navigation

import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor.CheckpointEditorFragment
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment
import com.gmail.maystruks08.nfcruntracker.ui.qr_code.ScanCodeFragment
import com.gmail.maystruks08.nfcruntracker.ui.race.RaceFragment
import com.gmail.maystruks08.nfcruntracker.ui.race.create.CreateRaceBottomShitFragment
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import com.gmail.maystruks08.nfcruntracker.ui.settings.SettingsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
object Screens {

    class RaceListScreen : AppScreen() {

        override fun getFragment() = RaceFragment.getInstance()

        companion object {
            fun tag() = RunnersFragment.name()
        }
    }

    class CreateRaceScreen : AppScreen() {

        override fun getFragment() = CreateRaceBottomShitFragment.getInstance()

        companion object {
            fun tag() = CreateRaceBottomShitFragment.name()
        }
    }

    @ExperimentalCoroutinesApi
    class RunnersScreen(
        private val raceId: String,
        private val raceName: String,
        private val firstDistanceId: String?
    ) : AppScreen() {

        override fun getFragment() = RunnersFragment.getInstance(raceId, raceName, firstDistanceId)

        companion object {
            fun tag() = RunnersFragment.name()
        }
    }

    class RunnerScreen(private val runnerNumber: Long, private val distanceId: String) : AppScreen() {
        override fun getFragment() = RunnerFragment.getInstance(runnerNumber, distanceId)

        companion object {
            fun tag() = RunnerScreen.name()
        }
    }

    class CheckpointEditorScreen() : AppScreen() {
        override fun getFragment() = CheckpointEditorFragment.getInstance()

        companion object {
            fun tag() = CheckpointEditorFragment.name()
        }
    }

    class SettingsScreen : AppScreen() {
        override fun getFragment() = SettingsFragment()

        companion object {
            fun tag() = SettingsFragment::class.java.simpleName
        }
    }

    class LoginScreen : AppScreen() {
        override fun getFragment() = LoginFragment.getInstance()

        companion object {
            fun tag() = LoginFragment.name()
        }
    }

    class RegisterNewRunnerScreen(private val raceId: String, private val distanceId: String) :
        AppScreen() {
        override fun getFragment() = RegisterNewRunnerFragment.getInstance(raceId, distanceId)

        companion object {
            fun tag() = RegisterNewRunnerFragment.name()
        }
    }

    class ScanCodeScreen(private val callback: (scannedCode: String) -> Unit) : AppScreen() {
        override fun getFragment() = ScanCodeFragment.getInstance(callback)

        companion object {
            fun tag() = ScanCodeFragment.name()
        }
    }
}

