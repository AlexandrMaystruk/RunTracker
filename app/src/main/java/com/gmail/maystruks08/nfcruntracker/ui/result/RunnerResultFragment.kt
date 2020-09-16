package com.gmail.maystruks08.nfcruntracker.ui.result

import android.text.InputType
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import kotlinx.android.synthetic.main.fragment_runners_results.*

class RunnerResultFragment : BaseFragment(R.layout.fragment_runners_results) {

    lateinit var viewModel: RunnerResultViewModel

    private lateinit var resultAdapter: ResultItemsAdapter

    override fun injectDependencies() {
        App.runnersResultComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withMenu(R.menu.menu_search)
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) { viewModel.onSearchQueryChanged(it) }
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runners_results)
        .build()

    override fun bindViewModel() {
        viewModel.runnerResults.observe(viewLifecycleOwner, { runnersResults ->
            resultAdapter.resultList = runnersResults.toMutableList()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            when(it){
                is RunnerNotFoundException -> context?.toast(getString(R.string.error_runner_not_found))
                is SaveRunnerDataException -> context?.toast(getString(R.string.error_save_data_to_local_db))
            }
        })
    }

    override fun initViews() {
        resultAdapter = ResultItemsAdapter()
        runnersResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnersResultsRecyclerView.context)
            adapter = resultAdapter
        }

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_runners -> viewModel.provideFinishers(RunnerType.NORMAL)
                R.id.item_iron_runners -> viewModel.provideFinishers(RunnerType.IRON)
            }
            return@setOnNavigationItemSelectedListener true
        }
        navigation.selectedItemId = R.id.item_runners
    }

    override fun onDestroyView() {
        runnersResultsRecyclerView.adapter = null
        App.clearRunnersResultComponent()
        super.onDestroyView()
    }
}
