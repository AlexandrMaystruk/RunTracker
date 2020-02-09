package com.gmail.maystruks08.nfcruntracker.ui.register

import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import javax.inject.Inject

class RegisterNewRunnerFragment : BaseFragment(R.layout.fragment_register_new_runner) {


    @Inject
    lateinit var viewModel: RegisterNewRunnerViewModel

    override fun injectDependencies() = App.registerNewRunnerComponent?.inject(this)

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {

    }

    override fun initViews() {
    }

    override fun onDestroyView() {
        App.clearRegisterNewRunnerComponent()
        super.onDestroyView()
    }

}
