package com.gmail.maystruks08.nfcruntracker.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.account.AssesLevel
import com.gmail.maystruks08.domain.interactors.use_cases.GetAccountAccessLevelUseCase
import com.gmail.maystruks08.domain.interactors.use_cases.LogOutUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class SettingsViewModel @ViewModelInject constructor(
    private val router: Router,
    private val getAccountAccessLevelUseCase: GetAccountAccessLevelUseCase,
    private val logOutUseCase: LogOutUseCase
) : BaseViewModel() {

    private var changeStartButtonVisibilityLiveData = MutableStateFlow<AssesLevel>(AssesLevel.User)
    val uiState
        get(): Flow<ViewState> = changeStartButtonVisibilityLiveData.map {
            when (it) {
                AssesLevel.Admin -> ViewState.ShowStartButton
                else -> ViewState.HideStartButton
            }
        }

    init {
        viewModelScope.launch {
            getAccountAccessLevelUseCase
                .invoke()
                .also {
                    changeStartButtonVisibilityLiveData.value = it
                }
        }
    }

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

    fun onBackClicked() {
        router.exit()
    }

    sealed class ViewState {
        object ShowStartButton : ViewState()
        object HideStartButton : ViewState()
    }
}