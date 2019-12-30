package com.gmail.maystruks08.nfcruntracker.ui

import androidx.lifecycle.Observer
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_root.*
import javax.inject.Inject

class RootFragment : BaseFragment(R.layout.fragment_root) {

    @Inject
    lateinit var viewModel: RootViewModel

    override fun injectDependencies() {
        App.rootComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder().withTitle(R.string.app_name).build()

    override fun bindViewModel() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })
    }

    override fun initViews() {
        addNewRunner.setOnClickListener {
            viewModel.onAddNewRunnerClicked()
        }
    }

    override fun onDestroyView() {
        App.clearRootComponent()
        super.onDestroyView()
    }
}
