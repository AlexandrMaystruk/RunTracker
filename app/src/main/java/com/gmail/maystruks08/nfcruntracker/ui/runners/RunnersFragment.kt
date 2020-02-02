package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.text.InputType
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_runners.*
import javax.inject.Inject

class RunnersFragment : BaseFragment(R.layout.fragment_runners) {

    @Inject
    lateinit var viewModel: RunnersViewModel

    private var runnerAdapter: RunnerAdapter? = null

    override fun injectDependencies() {
        App.runnersComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withTitle(R.string.app_name)
        .withMenu(R.menu.menu_search_with_settings)
        .withMenuItems(listOf(R.id.action_settings), listOf(MenuItem.OnMenuItemClickListener {
            viewModel.onOpenSettingsFragmentClicked()
            true
        }))
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) {
            viewModel.onSearchQueryChanged(it)
        }
        .build()

    override fun bindViewModel() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            context?.toast(it)
        })

        viewModel.runners.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.runnerList = it
        })

        viewModel.runnerUpdate.observe(viewLifecycleOwner, Observer {
            runnerAdapter?.updateItem(it)
        })
    }

    override fun initViews() {
        runnerAdapter = RunnerAdapter { viewModel.onRunnerClicked(it) }
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
}
