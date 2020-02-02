package com.gmail.maystruks08.nfcruntracker.core.navigation

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.Screen

open class AppScreen : Screen() {

    override fun getScreenKey(): String {
        return super.getScreenKey()
    }

    open fun getFragment(): Fragment? {
        return null
    }
}
