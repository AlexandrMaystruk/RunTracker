package com.gmail.maystruks08.nfcruntracker.ui.statistic

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentDistanceStatisticBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.main.dialogs.CheckpointAdapter
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DistanceStatisticFragment : BaseFragment(R.layout.fragment_distance_statistic) {

    private val viewModel: DistanceStatisticViewModel by viewModels()
    private var raceId: String by argument()
    private var distanceId: String by argument()
    private val binding: FragmentDistanceStatisticBinding by viewBinding {
        binding.rvCheckpoints.adapter = null
    }

    private var checkpointAdapter: AppAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkpointAdapter = AppAdapter(listOf(CheckpointStatisticHolderManager()))
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_distance_statistic)
        .build()

    override fun bindViewModel() {
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                distance
                    .collect {
                        if (it == null) return@collect
                        binding.tvDistanceName.text = it.name
                        binding.chartView.setChartItems(it.chartItems)
                    }
            }
            lifecycleScope.launchWhenStarted {
                showCheckpoints.collect {
                    checkpointAdapter?.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                showProgress.collect {

                }
            }
        }
    }

    override fun initViews() {
        viewModel.init(CurrentRaceDistance(raceId, distanceId))
        with(binding) {
            rvCheckpoints.adapter = checkpointAdapter
        }

    }

    companion object {
        fun getInstance(
            raceId: String,
            distanceId: String,
        ) = DistanceStatisticFragment().apply {
            this.raceId = raceId
            this.distanceId = distanceId
        }
    }
}