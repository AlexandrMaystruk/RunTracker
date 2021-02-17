package com.gmail.maystruks08.nfcruntracker.core.navigation

import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor.CheckpointEditorFragment
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment
import com.gmail.maystruks08.nfcruntracker.ui.race.RaceFragment
import com.gmail.maystruks08.nfcruntracker.ui.race.create.CreateRaceBottomShitFragment
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.result.RunnerResultFragment
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import com.gmail.maystruks08.nfcruntracker.ui.settings.SettingsFragment
import kotlinx.coroutines.ObsoleteCoroutinesApi

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

    class RunnersScreen(private val raceId: Long, private val firstDistanceId: Long?) : AppScreen() {

        override fun getFragment() = RunnersFragment.getInstance(raceId, firstDistanceId)

        companion object {
            fun tag() = RunnersFragment.name()
        }
    }

    class RunnerScreen(private val runnerNumber: Long, private val distanceId: Long) : AppScreen() {
        override fun getFragment() = RunnerFragment.getInstance(runnerNumber, distanceId)

        companion object {
            fun tag() = RunnerScreen.name()
        }
    }

    class RunnersResultsScreen : AppScreen() {
        override fun getFragment() = RunnerResultFragment()

        companion object {
            fun tag() = RunnersResultsScreen.name()
        }
    }


    class CheckpointEditorScreen() : AppScreen() {
        override fun getFragment() = CheckpointEditorFragment.getInstance()

        companion object {
            fun tag() = CheckpointEditorScreen.name()
        }
    }

    class SettingsScreen : AppScreen() {
        override fun getFragment() = SettingsFragment()

        companion object {
            fun tag() = SettingsScreen.name()
        }
    }

    class LoginScreen : AppScreen() {
        override fun getFragment() = LoginFragment.getInstance()

        companion object {
            fun tag() = LoginScreen.name()
        }
    }

    class RegisterNewRunnerScreen : AppScreen() {
        override fun getFragment() = RegisterNewRunnerFragment()

        companion object {
            fun tag() = RegisterNewRunnerScreen.name()
        }
    }
}

