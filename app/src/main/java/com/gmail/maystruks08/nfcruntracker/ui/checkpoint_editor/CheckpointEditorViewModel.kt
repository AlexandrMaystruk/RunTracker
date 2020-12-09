package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import ru.terrakok.cicerone.Router
import timber.log.Timber

class CheckpointEditorViewModel @ViewModelInject constructor(
    private val router: Router
) : BaseViewModel() {

    val showProgress: LiveData<Boolean> get() = _showProgressLiveData
    private val _showProgressLiveData = MutableLiveData<Boolean>()

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}