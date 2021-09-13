package com.gmail.maystruks08.nfcruntracker.ui.race.edit

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentDistanceEditorBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.CheckpointSwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.CreateNewCheckpointViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.EditCheckpointViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter.EditDistanceViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.DividerItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.view_models.DistanceView
import com.gmail.maystruks08.nfcruntracker.ui.view_models.EditCheckpointView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RaceEditorFragment : BaseFragment(R.layout.fragment_distance_editor),
    EditDistanceViewHolderManager.Interaction,
    EditCheckpointViewHolderManager.Interaction,
    CreateNewCheckpointViewHolderManager.Interaction{

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
        distanceCheckpointsAdapter = AppAdapter(listOf(
            EditCheckpointViewHolderManager(this),
            CreateNewCheckpointViewHolderManager(this)
        ))
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
        binding.rvDistanceType.addItemDecoration(
            DividerItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.margin_s
                )
            )
        )

        binding.rvDistanceCheckpoints.adapter = distanceCheckpointsAdapter
        setUpItemTouchHelper()
    }

    override fun onItemSelected(distance: DistanceView) {
        viewModel.changeDistance(distance.id)
    }

    override fun onCheckpointChanged(position: Int, item: EditCheckpointView) {
        viewModel.onCheckpointChanged(position, item)
    }

    override fun onCreateNewCheckpointClicked(position: Int) {
        viewModel.onCreateNewCheckpointClicked(position)
    }

    private fun setUpItemTouchHelper() {
        val orderSwipeActionHelper = object : CheckpointSwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val editCheckpointView = distanceCheckpointsAdapter.currentList[position] as? EditCheckpointView
                editCheckpointView ?: return
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onCheckpointSwipedLeft(position, editCheckpointView)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(binding.rvDistanceCheckpoints)
    }


    companion object {
        fun getInstance() = RaceEditorFragment()
    }
}
