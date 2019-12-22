package com.gmail.maystruks08.nfcruntracker.core.base

interface IView {

    fun showLoading ()

    fun hideLoading ()

    fun showError (t: Throwable)

}