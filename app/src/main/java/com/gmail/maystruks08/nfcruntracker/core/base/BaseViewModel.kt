package com.gmail.maystruks08.nfcruntracker.core.base

import androidx.lifecycle.*

abstract class BaseViewModel : ViewModel() {

    val toast get() = toastLiveData

    protected val toastLiveData = MutableLiveData<String>()

}