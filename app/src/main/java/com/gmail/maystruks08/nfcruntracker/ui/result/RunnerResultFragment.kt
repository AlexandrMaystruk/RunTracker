package com.gmail.maystruks08.nfcruntracker.ui.result

import android.text.InputType
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.toast
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnersResultsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RunnerResultFragment : BaseFragment(R.layout.fragment_runners_results) {

    private val viewModel: RunnerResultViewModel by viewModels()
    private val binding: FragmentRunnersResultsBinding by viewBinding {
        runnersResultsRecyclerView.adapter = null
    }
    private lateinit var resultAdapter: ResultItemsAdapter

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withMenu(R.menu.menu_search)
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) { viewModel.onSearchQueryChanged(it) }
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runners_results)
        .build()

    override fun bindViewModel() {
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                runnerResults.collect { runnersResults ->
                    resultAdapter.resultList = runnersResults.toMutableList()
                }
            }
            lifecycleScope.launchWhenResumed {
                error.collect {
                    when (it) {
                        is RunnerNotFoundException -> context?.toast(getString(R.string.error_runner_not_found))
                        is SaveRunnerDataException -> context?.toast(getString(R.string.error_save_data_to_local_db))
                    }
                }
            }
        }
    }

    override fun initViews() {
        resultAdapter = ResultItemsAdapter()
        with(binding) {
            runnersResultsRecyclerView.adapter = resultAdapter

            //TODO refactor
            navigation.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.item_runners -> viewModel.provideFinishers("0")
                    R.id.item_iron_runners -> viewModel.provideFinishers("1")
                }
                return@setOnNavigationItemSelectedListener true
            }
            navigation.selectedItemId = R.id.item_runners
        }
    }

}
