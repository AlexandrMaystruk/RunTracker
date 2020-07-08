package com.gmail.maystruks08.nfcruntracker.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment.Companion.RC_SIGN_IN
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val router: Router,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    val startAuthFlow get() = startAuthFlowLiveData

    private val startAuthFlowLiveData = MutableLiveData<Unit>()

    init {
        if (FirebaseAuth.getInstance().currentUser == null) startAuthFlowLiveData.postValue(Unit) else updateAndStartMainScreen()
    }

    private fun updateAndStartMainScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = settingsRepository.updateConfig()) {
                is ResultOfTask.Value -> {
                    when (val task = settingsRepository.getCachedConfig()) {
                        is ResultOfTask.Value -> withContext(Dispatchers.Main) {
                            router.newRootScreen(Screens.RootRunnersScreen())
                        }
                        is ResultOfTask.Error -> {
                            Timber.e(task.error)
                            withContext(Dispatchers.Main) { router.newRootScreen(Screens.RootRunnersScreen()) }
                        }
                    }
                }
                is ResultOfTask.Error -> {
                    Timber.e(resultOfTask.error)
                    withContext(Dispatchers.Main) { router.newRootScreen(Screens.RootRunnersScreen()) }
                }
            }
        }
    }

    fun handleLoginResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> updateAndStartMainScreen()
                Activity.RESULT_CANCELED -> router.exit()
                else -> toastLiveData.postValue(IdpResponse.fromResultIntent(data)?.error?.localizedMessage)
            }
        }
    }
}