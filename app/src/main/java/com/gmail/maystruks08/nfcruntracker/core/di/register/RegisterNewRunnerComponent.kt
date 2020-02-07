package com.gmail.maystruks08.nfcruntracker.core.di.register

import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import dagger.Subcomponent

@Subcomponent(modules = [RegisterNewRunnerModule::class])
@RegisterScope
interface RegisterNewRunnerComponent {

    fun inject(fragment: RegisterNewRunnerFragment)

}