package com.gmail.maystruks08.nfcruntracker.ui.race.edit

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentDistanceEditorBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.EditCheckpointViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.EditDistanceViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.DividerItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.DividerVerticalItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.view_models.DistanceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RaceEditorFragment : BaseFragment(R.layout.fragment_distance_editor),
    EditDistanceViewHolderManager.Interaction,
    EditCheckpointViewHolderManager.Interaction {

    private lateinit var distanceAdapter: AppAdapter
    private lateinit var distanceCheckpointsAdapter: AppAdapter

    private val viewModel: RaceEditorViewModel by viewModels()
    private val binding: FragmentDistanceEditorBinding by viewBinding {
        binding.rvDistanceType.adapter = null
        binding.rvDistanceCheckpoints.adapter = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        distanceAdapter = AppAdapter(listOf(EditDistanceViewHolderManager(this)))
        distanceCheckpointsAdapter = AppAdapter(listOf(EditCheckpointViewHolderManager(this)))
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle("Edit race")
        .withMenu(R.menu.menu_save)
        .withMenuItems(
            listOf(R.id.action_save_changes),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onSaveChangedClicked()
                true
            })
        )
        .build()

    override fun bindViewModel() {
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                distances.collect {
                    distanceAdapter.submitList(it)
                }
            }
            lifecycleScope.launchWhenStarted {
                checkpoints.collect {
                    distanceCheckpointsAdapter.submitList(it)
                }
            }
        }
    }

    override fun initViews() {
        binding.rvDistanceType.adapter = distanceAdapter
        binding.rvDistanceType.addItemDecoration(DividerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))

        binding.rvDistanceCheckpoints.adapter = distanceCheckpointsAdapter
        binding.rvDistanceCheckpoints.addItemDecoration(DividerVerticalItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
    }

    override fun onItemSelected(distance: DistanceView) {
        viewModel.changeDistance(distance.id)
    }


    companion object {
        fun getInstance() = RaceEditorFragment()
    }
}
