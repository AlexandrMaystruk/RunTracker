package com.gmail.maystruks08.nfcruntracker.ui

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var runnerAdapter: RunnerAdapter

    override fun injectDependencies() {
        App.rootComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder().withTitle(R.string.app_name).build()

    override fun bindViewModel() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })

        viewModel.runners.observe(viewLifecycleOwner, Observer {
            runnerAdapter.runnerList = it
        })
    }

    override fun initViews() {
        runnerAdapter = RunnerAdapter {}
        runnersRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnersRecyclerView.context)
            adapter = runnerAdapter
        }
        showAllRunners.setOnClickListener {
            viewModel.showAllRunnerClicked()
        }
    }

    override fun onDestroyView() {
        App.clearRootComponent()
        super.onDestroyView()
    }
}
