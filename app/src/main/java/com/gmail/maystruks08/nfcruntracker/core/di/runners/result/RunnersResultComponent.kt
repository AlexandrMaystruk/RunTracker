package com.gmail.maystruks08.nfcruntracker.core.di.runners.result

import com.gmail.maystruks08.nfcruntracker.ui.result.RunnerResultFragment
import dagger.Subcomponent

@Subcomponent(modules = [RunnersResultModule::class])
@RunnersResultsScope
interface RunnersResultComponent {

    fun inject(fragment: RunnerResultFragment)

}