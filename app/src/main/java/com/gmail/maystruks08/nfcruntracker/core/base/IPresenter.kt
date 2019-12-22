package com.gmail.maystruks08.nfcruntracker.core.base


interface IPresenter <T: IView> {

    var view: T?

    fun bindView(view: T)

    fun onBackClicked()

    fun end ()

}