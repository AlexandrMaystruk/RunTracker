package com.gmail.maystruks08.nfcruntracker.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.setVisibility
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import javax.inject.Inject

class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    override fun injectDependencies() {
        App.loginComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLoginBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {
        viewModel.run {
            startAuthFlow.observe(viewLifecycleOwner) { type ->
                when (type) {
                    is Google -> startActivityForSingInWithGoogle()
                    is EmailAndPassword -> changeInputFieldsVisibility(
                        isVisible = true,
                        isCreateNewUser = type.isRegisterNewUser
                    )
                    else -> Unit
                }
            }
            showProgress.observe(viewLifecycleOwner) { binding.progressBar.setVisibility(it) }
        }
    }

    override fun initViews() {
        viewModel.initView()
        with(binding) {
            ivLoginWithGoogle.setOnClickListener { viewModel.signInWithGoogle() }
            ivLoginWithEmail.setOnClickListener { viewModel.signInWithEmailClicked() }
            signUp.setOnClickListener { viewModel.registerNewUserCommand() }
            etUserName.addTextChangedListener { validateEmail(it.toString()) }
            etUserPassword.addTextChangedListener { validatePassword(it.toString()) }
            btnOptional.setOnClickListener {
                val email = etUserName.text.toString()
                val password = etUserPassword.text.toString()
                if (validateEmail(email) != null && validatePassword(password) != null) {
                    viewModel.onOptionsButtonClicked(email, password)
                } else {
                    requireContext().toast("Login or password not valid")
                }
            }
            btnBack.setOnClickListener {
                changeInputFieldsVisibility(
                    isVisible = false,
                    isCreateNewUser = false
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleLoginResult(requestCode, data)
    }

    private fun validateEmail(email: String): String? {
        return email
    }

    private fun validatePassword(password: String): String? {
        return password
    }

    private fun startActivityForSingInWithGoogle() {
        changeInputFieldsVisibility(isVisible = false, isCreateNewUser = false)
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun changeInputFieldsVisibility(isVisible: Boolean, isCreateNewUser: Boolean) {
        with(binding) {
            etUserName.setText("")
            etUserPassword.setText("")
            etUserPassword.addTextChangedListener { validatePassword(it.toString()) }
            if (isVisible) {
                emailAndPasswordGroup.visibility = View.VISIBLE
                loginTypesGroup.visibility = View.GONE
                if (isCreateNewUser) {
                    btnOptional.text = getString(R.string.signUp)
                    signUpText.visibility = View.GONE
                    signUp.visibility = View.GONE
                } else {
                    btnOptional.text = getString(R.string.signIn)
                    signUpText.visibility = View.VISIBLE
                    signUp.visibility = View.VISIBLE
                }
            } else {
                emailAndPasswordGroup.visibility = View.GONE
                loginTypesGroup.visibility = View.VISIBLE
            }
        }
    }

    override fun clearInjectedComponents() = App.clearLoginComponent()

    companion object {

        fun getInstance() = LoginFragment()

        const val RC_SIGN_IN = 777

    }
}
