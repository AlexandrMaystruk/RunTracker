package com.gmail.maystruks08.nfcruntracker.core.di.runner

import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import dagger.Subcomponent

@Subcomponent(modules = [RunnerModule::class])
@RunnerScope
interface RunnerComponent {

    fun inject(fragment: RunnerFragment)

}