package com.gmail.maystruks08.nfcruntracker.ui

import androidx.lifecycle.*
import javax.inject.Inject

class RootViewModel @Inject constructor() : ViewModel() {

    private val toastLiveData = MutableLiveData<String>()

    val toast
    get() = toastLiveData


    fun onAddNewRunnerClicked() {
        toastLiveData.value = "Hello world with MVVM"
    }
}