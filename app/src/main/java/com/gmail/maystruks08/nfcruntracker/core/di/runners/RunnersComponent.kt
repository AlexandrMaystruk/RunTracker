package com.gmail.maystruks08.nfcruntracker.core.di.runners

import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import dagger.Subcomponent

@Subcomponent(modules = [RunnersModule::class])
@RunnersScope
interface RunnersComponent {

    fun inject(fragment: RunnersFragment)

}