package com.gmail.maystruks08.nfcruntracker.core.di.runners

import com.gmail.maystruks08.nfcruntracker.core.di.register.RegisterNewRunnerComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.result.RunnersResultComponent
import com.gmail.maystruks08.nfcruntracker.core.di.runners.runner.RunnerComponent
import com.gmail.maystruks08.nfcruntracker.ui.runners.RunnersFragment
import dagger.Subcomponent

@Subcomponent(modules = [RunnersModule::class])
@RunnersScope
interface RunnersComponent {

    fun inject(fragment: RunnersFragment)

    fun provideRunnerComponent(): RunnerComponent

    fun provideRunnerResultComponent(): RunnersResultComponent

    fun provideRegisterRunnerComponent(): RegisterNewRunnerComponent

}