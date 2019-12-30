package com.gmail.maystruks08.nfcruntracker.core.di.root

import com.gmail.maystruks08.nfcruntracker.ui.RootFragment
import dagger.Subcomponent

@Subcomponent(modules = [RootModule::class])
@RootScope
interface RootComponent {

    fun inject(fragment: RootFragment)

}