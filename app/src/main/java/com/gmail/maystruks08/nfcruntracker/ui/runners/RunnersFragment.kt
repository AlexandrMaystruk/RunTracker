package com.gmail.maystruks08.nfcruntracker.ui.runners

import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_runners.*
import javax.inject.Inject

class RunnersFragment : BaseFragment(R.layout.fragment_runners) {

    /**
     * Created RunnersViewModel without ViewModel providers
     * because I don't know how I can create different instances.
     * If used ViewModel providers for a different instance of the fragment
     * will injected the same instance of RunnersViewModel
     */
    @Inject
    lateinit var viewModel: RunnersViewModel

    private var runnerAdapter: RunnerAdapter? = null

    private var runnerTypeId: Int by argument()

    private lateinit var onClickedAtRunner: (RunnerView) -> Unit

    override fun injectDependencies() {
        App.runnersComponent?.inject(this)
        viewModel.initFragment(runnerTypeId)
    }

    override fun initToolbar() = FragmentToolbar.Builder().build()

    override fun bindViewModel() {
        viewModel.runners.observe(viewLifecycleOwner, {
            runnerAdapter?.runnerList = it
        })

        viewModel.runnerAdd.observe(viewLifecycleOwner, {
            runnerAdapter?.insertItemOrUpdateIfExist(it)
        })

        viewModel.runnerUpdate.observe(viewLifecycleOwner, {
            runnerAdapter?.updateItem(it)
        })

        viewModel.runnerRemove.observe(viewLifecycleOwner, {
            runnerAdapter?.removeItem(it)
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            val checkpointName = it.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message).show(childFragmentManager, SuccessDialogFragment.name())
        })
    }

    override fun initViews() {
        runnerAdapter = RunnerAdapter { onClickedAtRunner.invoke(it) }
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

    companion object {

        fun getInstance(runnerTypeId: Int, onClickedAtRunner: (RunnerView) -> Unit) =
            RunnersFragment().apply {
                this.onClickedAtRunner = onClickedAtRunner
                this.runnerTypeId = runnerTypeId
            }
    }
}
