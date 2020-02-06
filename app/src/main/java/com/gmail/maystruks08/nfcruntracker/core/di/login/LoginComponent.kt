package com.gmail.maystruks08.nfcruntracker.core.di.login

import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment
import dagger.Subcomponent

@Subcomponent(modules = [LoginModule::class])
@LoginScope
interface LoginComponent {

    fun inject(fragment: LoginFragment)

}