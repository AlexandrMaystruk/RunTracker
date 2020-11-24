package com.gmail.maystruks08.nfcruntracker.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    val toast get(): LiveData<String>  = toastLiveData

    protected val toastLiveData = SingleLiveEvent<String>()

}