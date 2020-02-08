package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_runners.*
import javax.inject.Inject

class RunnersFragment : BaseFragment(R.layout.fragment_runners) {

    @Inject
    lateinit var viewModel: RunnersViewModel

    private var runnerAdapter: RunnerAdapter? = null

    private var runnerTypeId: Int by argument()

    private lateinit var onClickedAtRunner: (RunnerView) -> Unit

    override fun injectDependencies() {
        App.runnersComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {
        viewModel.initFragment(runnerTypeId)

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })

        viewModel.runners.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.runnerList = it
        })

        viewModel.runnerAdd.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.insertItemOrUpdateIfExist(it)
        })

        viewModel.runnerUpdate.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.updateItem(it)
        })

        viewModel.runnerRemove.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.removeItem(it)
        })
    }

    override fun initViews() {
        runnerAdapter = RunnerAdapter{ onClickedAtRunner.invoke(it) }
        runnersRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnersRecyclerView.context)
            adapter = runnerAdapter
        }
    }

    override fun onDestroyView() {
        runnersRecyclerView.adapter = null
        runnerAdapter = null
        App.clearRunnersComponent()
        super.onDestroyView()
    }

    companion object{

        fun getInstance(runnerTypeId: Int, onClickedAtRunner: (RunnerView) -> Unit) = RunnersFragment().apply {
            this.onClickedAtRunner = onClickedAtRunner
            this.runnerTypeId = runnerTypeId }
    }
}
