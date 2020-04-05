package com.gmail.maystruks08.nfcruntracker.ui.result

import android.text.InputType
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import kotlinx.android.synthetic.main.fragment_runners_results.*
import javax.inject.Inject

class RunnerResultFragment : BaseFragment(R.layout.fragment_runners_results) {

    @Inject
    lateinit var viewModel: RunnerResultViewModel

    private lateinit var resultAdapter: ResultItemsAdapter

    override fun injectDependencies() {
        App.runnersResultComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withMenu(R.menu.menu_search)
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) {
          viewModel.onSearchQueryChanged(it)
        }
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runners_results)
        .build()

    override fun bindViewModel() {
        viewModel.runnerResults.observe(viewLifecycleOwner, Observer { runnersResults ->
            resultAdapter.resultList = runnersResults.toMutableList()
        })
    }

    override fun initViews() {
        resultAdapter = ResultItemsAdapter()
        runnersResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnersResultsRecyclerView.context)
            adapter = resultAdapter
        }
    }

    override fun onDestroyView() {
        App.clearRunnersResultComponent()
        super.onDestroyView()
    }

}
