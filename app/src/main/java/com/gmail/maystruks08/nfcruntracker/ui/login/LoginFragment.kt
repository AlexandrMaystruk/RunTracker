package com.gmail.maystruks08.nfcruntracker.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.DaggerViewModelFactory
import javax.inject.Inject

class LoginFragment : BaseFragment(R.layout.fui_auth_method_picker_layout) {

    @Inject
    lateinit var viewModeFactory: DaggerViewModelFactory

    lateinit var viewModel: LoginViewModel

    override fun injectDependencies() {
        App.loginComponent?.inject(this)
        viewModel = ViewModelProviders.of(this, this.viewModeFactory).get(LoginViewModel::class.java)
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {
        viewModel.startAuthFlow.observe(viewLifecycleOwner, Observer {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        arrayListOf(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build()
                        )
                    )
                    .setTheme(R.style.LoginTheme)
                    .build(),
                RC_SIGN_IN
            )
        })
    }

    override fun initViews() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleLoginResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        App.clearLoginComponent()
        super.onDestroyView()
    }

    companion object {

        const val RC_SIGN_IN = 777
    }
}
