package com.gmail.maystruks08.nfcruntracker.ui.login

import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.gmail.maystruks08.nfcruntracker.ui.login.LoginFragment.Companion.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.ObsoleteCoroutinesApi
import ru.terrakok.cicerone.Router
import timber.log.Timber

@ObsoleteCoroutinesApi
class LoginViewModel @ViewModelInject constructor(
    private val router: Router,
    private val configPreferences: ConfigPreferences
) : BaseViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val startAuthFlow: LiveData<AuthState> get() = _startAuthFlowLiveData
    private val _startAuthFlowLiveData = SingleLiveEvent<AuthState>()

    val showProgress: LiveData<Boolean> get() = _showProgressLiveData
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()

    fun initView() {
        if (auth.currentUser != null) navigateToFragment()
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
                    navigateToFragment()
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
                    navigateToFragment()
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
                    navigateToFragment()
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

    private fun navigateToFragment() {
        val raceId = configPreferences.getRaceId()
        if (raceId != -1L) {
            router.newRootScreen(Screens.RunnersScreen(raceId, null))
        } else {
            router.newRootScreen(Screens.RaceListScreen())
        }
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable)
    }
}

class SignInWithCredentialExceptions : Throwable()