package com.gmail.maystruks08.nfcruntracker.core.di.runners.root

import com.gmail.maystruks08.nfcruntracker.core.di.register.RegisterNewRunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.RunnersComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.runner.RunnerComponent
import com.gmail.maystruks08.nfcruntracker.ui.runners.RootRunnersFragment
import dagger.Subcomponent

@Subcomponent(modules = [RootRunnersModule::class])
@RootRunnersScope

interface RootRunnersComponent {

    fun inject(fragment: RootRunnersFragment)

    fun provideRunnersComponent(): RunnersComponent

    fun provideRegisterRunnerComponent(): RegisterNewRunnerComponent

}