package com.gmail.maystruks08.nfcruntracker.ui.main

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.transition.TransitionManager
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.*
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentMainBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.DividerItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.DividerVerticalItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.RunnerSwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers.*
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamView
import com.gmail.maystruks08.nfcruntracker.ui.main.dialogs.SelectCheckpointDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.main.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.main.utils.CircleMenuEvent
import com.gmail.maystruks08.nfcruntracker.ui.main.utils.CircleMenuManager
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.view_models.DistanceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect


@FlowPreview
@ObsoleteCoroutinesApi
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainScreenFragment : BaseFragment(R.layout.fragment_main),
    RunnerViewHolderManager.Interaction,
    DistanceViewHolderManager.Interaction,
    TeamViewHolderManager.Interaction {

    private val viewModel: MainScreenViewModel by viewModels()

    private val binding: FragmentMainBinding by viewBinding {
        rvRunners.adapter = null
        rvDistanceType.adapter = null
        circleMenuLayoutManager = null
    }
    private lateinit var runnerAdapter: AppAdapter
    private lateinit var distanceAdapter: AppAdapter
    private var circleMenuLayoutManager: CircleMenuManager? = null


    private var alertDialog: AlertDialog? = null

    private var raceId: String by argument()
    private var raceName: String by argument()

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withTitle(raceName)
        .withMenu(R.menu.menu_search_with_settings)
        .withMenuItems(
            listOf(R.id.action_settings, R.id.action_result, R.id.action_select_race, R.id.action_edit_race),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onOpenSettingsFragmentClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.changeMode()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onSelectRaceClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                    viewModel.onEditCurrentRaceClicked()
                    true
            })
        )
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) {
            viewModel.onSearchQueryChanged(it)
        }
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runnerAdapter = AppAdapter(
            listOf(
                RunnerViewHolderManager(this),
                TeamViewHolderManager(this),
                ResultViewHolderManager(),
                TeamResultViewHolderManager()
            )
        )
        distanceAdapter = AppAdapter(listOf(DistanceViewHolderManager(this)))
    }

    override fun bindViewModel() {
        with(viewModel) {
            lifecycleScope.launchWhenResumed {
                distance.collect {
                    distanceAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenResumed {
                runners.collect {
                    runnerAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenResumed {
                enableSelectCheckpointButton.collect { enable ->
                    TransitionManager.beginDelayedTransition(binding.root)
                    binding.tvCurrentCheckpoint.setVisibility(enable)
                    binding.tvRunnersTitle.text = if (enable) {
                        requireContext().string(R.string.current_checkpoint)
                    } else {
                        requireContext().string(R.string.finishers)
                    }
                }
            }

            lifecycleScope.launchWhenResumed {
                showProgress.collect { isNeedToShow ->
                    binding.progress.setVisibility(isNeedToShow)
                }
            }

            lifecycleScope.launchWhenResumed {
                showConfirmationDialog.collect {
                    alertDialog?.dismiss()
                    when (it) {
                        is AlertTypeConfirmOfftrack -> showConfirmOffTrackDialog(it.position)
                        is AlertTypeMarkRunnerAtCheckpoint -> showConfirmMarkRunnerAtCheckpointDialog(
                            it.position
                        )
                    }
                }
            }

            lifecycleScope.launchWhenResumed {
                showSuccessDialog.collect {
                    val checkpointName = it.first?.getName() ?: ""
                    val message = getString(R.string.success_message, checkpointName, "#${it.second}")
                    SuccessDialogFragment.getInstance(message)
                        .show(childFragmentManager, SuccessDialogFragment.name())
                }
            }

            lifecycleScope.launchWhenResumed {
                showSelectCheckpointDialog.collect { raceDistanceIds ->
                    val dialog = findFragmentByTag<SelectCheckpointDialogFragment>(SelectCheckpointDialogFragment.name())
                    if (dialog == null) {
                        SelectCheckpointDialogFragment
                            .getInstance(raceDistanceIds.first, raceDistanceIds.second) {
                                viewModel.onNewCurrentCheckpointSelected(it)
                            }
                            .show(childFragmentManager, SelectCheckpointDialogFragment.name())
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                closeSelectCheckpointDialog.collect { selectedCheckpoint ->
                    findFragmentByTag<SelectCheckpointDialogFragment>(SelectCheckpointDialogFragment.name())?.dismiss()
                    binding.tvCurrentCheckpoint.text = selectedCheckpoint
                }
            }

            lifecycleScope.launchWhenStarted {
                showTime.collect {
                    binding.tvTime.text = getString(R.string.competition_time, it)
                }
            }
        }
    }

    override fun initViews() {
        with(binding) {
            rvRunners.apply {
                addItemDecoration(DividerVerticalItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                adapter = runnerAdapter
            }
            rvDistanceType.apply {
                addItemDecoration(DividerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                adapter = distanceAdapter
            }

            tvCurrentCheckpoint.setOnClickListener { viewModel.onCurrentCheckpointTextClicked() }
            circleMenuLayoutManager = CircleMenuManager(binding.circleMenu) {
                when (it) {
                    CircleMenuEvent.CLICKED_REGISTER_NEW_RUNNER -> viewModel.onRegisterNewRunnerClicked()
                    CircleMenuEvent.CLICKED_SCAN_QR_CODE -> viewModel.onScanQRCodeClicked()
                }
            }
        }
        setUpItemTouchHelper()
    }

    private fun setUpItemTouchHelper() {
        val orderSwipeActionHelper = object : RunnerSwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedRunner = runnerAdapter.currentList[position] as? RunnerView
                swipedRunner ?: return
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onRunnerSwipedLeft(position, swipedRunner)
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.onRunnerSwipedRight(position, swipedRunner)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(binding.rvRunners)
    }

    override fun onItemSelected(item: RunnerView) {
        viewModel.onClickedAtRunner(item.id)
    }

    override fun onItemSelected(team: TeamView) {
        viewModel.onClickedAtTeam(team)
    }

    override fun onRunnerSwipedLeft(position: Int, swipedRunner: RunnerView) {
        viewModel.onRunnerSwipedLeft(position, swipedRunner)
    }

    override fun onRunnerSwipedRight(position: Int, swipedRunner: RunnerView) {
        viewModel.onRunnerSwipedRight(position, swipedRunner)
    }

    override fun onItemSelected(distance: DistanceView) {
        viewModel.changeDistance(distance.id)
    }

    fun onNfcCardScanned(cardId: String) {
        viewModel.onNfcCardScanned(cardId)
    }

    private fun showConfirmOffTrackDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.alert_confirm_offtrack_runner))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.onRunnerOffTrack()
                runnerAdapter.notifyItemChanged(position)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                runnerAdapter.notifyItemChanged(position)
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()

    }

    private fun showConfirmMarkRunnerAtCheckpointDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.mark_runner_without_card))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.markCheckpointAsPassed()
                runnerAdapter.notifyItemChanged(position)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                runnerAdapter.notifyItemChanged(position)
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()
    }

    private fun hideSoftKeyboard(imm: InputMethodManager) {
        toolbarManager?.clearSearch()
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    private val inputManager: InputMethodManager by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onStop() {
        super.onStop()
        hideSoftKeyboard(inputManager)
    }

    companion object {

        fun getInstance(raceId: String, raceName: String) =
            MainScreenFragment().apply {
                this.raceId = raceId
                this.raceName = raceName
            }
    }
}
