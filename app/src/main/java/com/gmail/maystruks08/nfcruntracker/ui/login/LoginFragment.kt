package com.gmail.maystruks08.nfcruntracker.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel

class LoginFragment : BaseFragment(R.layout.fui_auth_method_picker_layout) {

    lateinit var viewModel: LoginViewModel

    override fun injectDependencies() {
        App.loginComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {
        viewModel.startAuthFlow.observe(viewLifecycleOwner, {
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

    override fun initViews() {}

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
