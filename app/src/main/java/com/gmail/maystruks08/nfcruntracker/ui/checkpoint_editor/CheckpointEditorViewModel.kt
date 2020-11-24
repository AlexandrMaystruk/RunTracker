package com.gmail.maystruks08.nfcruntracker.ui.checkpoint_editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val router: Router,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    val showProgress: LiveData<Boolean> get() = _showProgressLiveData
    private val _showProgressLiveData = MutableLiveData<Boolean>()

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}