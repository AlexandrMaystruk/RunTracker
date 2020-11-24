package com.gmail.maystruks08.nfcruntracker.ui.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment.Companion.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    private val auth = FirebaseAuth.getInstance()

    val startAuthFlow: LiveData<AuthState> get() = _startAuthFlowLiveData
    private val _startAuthFlowLiveData = SingleLiveEvent<AuthState>()

    val showProgress: LiveData<Boolean> get() = _showProgressLiveData
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()

    fun initView() {
        if (auth.currentUser != null) onOperationSuccess()
    }

    fun signInWithGoogle() {
        _showProgressLiveData.value = true
        _startAuthFlowLiveData.value = Google
    }

    fun signInWithEmailClicked() {
        _startAuthFlowLiveData.value = EmailAndPassword(false)
    }

    fun registerNewUserCommand() {
        _startAuthFlowLiveData.value = EmailAndPassword(true)
    }

    fun onOptionsButtonClicked(email: String, password: String) {
        _showProgressLiveData.value = true
        val authMode = startAuthFlow.value
        when {
            authMode is EmailAndPassword && authMode.isRegisterNewUser -> {
                createFirebaseUserWithEmailAndPassword(email, password)
            }
            authMode is EmailAndPassword && !authMode.isRegisterNewUser -> {
                firebaseAuthWithEmailAndPassword(email, password)
            }
        }
    }

    fun handleLoginResult(requestCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                _showProgressLiveData.value = false
                toastLiveData.postValue("Google sign in failed")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onOperationSuccess()
                } else {
                    _showProgressLiveData.postValue(false)
                    handleError(SignInWithCredentialExceptions())
                }
            }
    }

    private fun firebaseAuthWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onOperationSuccess()
                } else {
                    _showProgressLiveData.postValue(false)
                    handleError(SignInWithCredentialExceptions())
                }
            }
    }

    private fun createFirebaseUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onOperationSuccess()
                } else {
                    _showProgressLiveData.postValue(false)
                    handleError(SignInWithCredentialExceptions())
                }
            }
            .addOnFailureListener {
                _showProgressLiveData.postValue(false)
                handleError(it)
            }

    }

    private fun onOperationSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val resultOfTask = settingsRepository.updateConfig()) {
                is ResultOfTask.Value -> {
                    when (val task = settingsRepository.getCachedConfig()) {
                        is ResultOfTask.Value -> withContext(Dispatchers.Main) {
                            _showProgressLiveData.postValue(false)
                            router.newRootScreen(Screens.RunnersScreen(0))
                        }
                        is ResultOfTask.Error -> {
                            _showProgressLiveData.postValue(false)
                            Timber.e(task.error)
                            withContext(Dispatchers.Main) { router.newRootScreen(Screens.RunnersScreen(0)) }
                        }
                    }
                }
                is ResultOfTask.Error -> {
                    _showProgressLiveData.postValue(false)
                    Timber.e(resultOfTask.error)
                    withContext(Dispatchers.Main) { router.newRootScreen(Screens.RunnersScreen(0)) }
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}

class SignInWithCredentialExceptions : Throwable()