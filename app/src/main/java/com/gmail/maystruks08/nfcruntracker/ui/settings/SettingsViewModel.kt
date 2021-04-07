package com.gmail.maystruks08.nfcruntracker.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.interactors.LogOutUseCase
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SettingsViewModel @ViewModelInject constructor(
    private val router: Router,
    private val repository: SettingsRepository,
    private val logOutUseCase: LogOutUseCase
) : BaseViewModel() {

    val changeStartButtonVisibility get(): LiveData<Boolean> = changeStartButtonVisibilityLiveData
    private val changeStartButtonVisibilityLiveData = SingleLiveEvent<Boolean>()


    fun onSignOutClicked() {
        viewModelScope.launch {
            val isSuccess = logOutUseCase.logout()
            if (isSuccess) {
                router.newRootScreen(Screens.LoginScreen())
            } else {
                toastLiveData.postValue("Logout error")
            }
        }
    }

    private fun resolveStartButtonVisibility(){
        val isCurrentUserAdmin = repository.getAdminUserIds().contains(FirebaseAuth.getInstance().currentUser?.uid)
        changeStartButtonVisibilityLiveData.postValue(isCurrentUserAdmin)
    }

    fun onBackClicked() {
        router.exit()
    }
}