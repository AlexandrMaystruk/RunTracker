package com.gmail.maystruks08.nfcruntracker.core.navigation

import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import com.gmail.maystruks08.nfcruntracker.ui.settings.SettingsFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView

object Screens {

    class RunnersScreen : AppScreen() {
        override fun getFragment() = RunnersFragment()

        companion object {
            fun tag() = RunnersScreen::class.java.canonicalName ?: ""
        }
    }

    class RunnerScreen(private val runner: RunnerView) : AppScreen() {
        override fun getFragment() = RunnerFragment.getInstance(runner)

        companion object {
            fun tag() = RunnerScreen::class.java.canonicalName ?: ""
        }
    }

    class SettingsScreen : AppScreen() {
        override fun getFragment() = SettingsFragment()

        companion object {
            fun tag() = SettingsScreen::class.java.canonicalName ?: ""
        }
    }

}

