//package com.gmail.maystruks08.nfcruntracker.core.base
//
//import javax.inject.Inject
//
//abstract class BasePresenter<T : IView> : IPresenter<T> {
//
//    @Inject
//    override var view: T? = null
//
//    override fun bindView(view: T) {
//        this.view = view
//    }
//
//    override fun onBackClicked(){
//        view = null
//    }
//
//    override fun end() {
//        view = null
//    }
//}