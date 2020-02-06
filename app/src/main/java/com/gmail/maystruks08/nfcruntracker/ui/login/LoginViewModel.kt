package com.gmail.maystruks08.nfcruntracker.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment.Companion.RC_SIGN_IN
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val router: Router, private val settingsRepository: SettingsRepository) : BaseViewModel() {

    val startAuthFlow get() = startAuthFlowLiveData

    private val startAuthFlowLiveData = MutableLiveData<Int>()

    init {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startAuthFlowLiveData.postValue(0)
        } else {
            viewModelScope.launch {
                try {
                    settingsRepository.updateConfig()
                } catch (e: Exception){
                    e.printStackTrace()
                }
                router.newRootScreen(Screens.RunnersScreen())
            }
        }
    }

    fun handleLoginResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                toastLiveData.postValue("Login successful")
                router.newRootScreen(Screens.RunnersScreen())
            } else {
                toastLiveData.postValue(IdpResponse.fromResultIntent(data)?.error?.localizedMessage)
            }
        }
    }
}