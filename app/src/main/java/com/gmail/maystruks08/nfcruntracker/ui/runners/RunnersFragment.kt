package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.text.InputType
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.argumentNullable
import com.gmail.maystruks08.nfcruntracker.core.ext.findFragmentByTag
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnersBinding
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.*
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SelectCheckpointDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect


@ObsoleteCoroutinesApi
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RunnersFragment : BaseFragment(R.layout.fragment_runners), RunnerListAdapter.Interaction,
    DistanceListAdapter.Interaction {

    private val viewModel: RunnersViewModel by viewModels()

    private val binding: FragmentRunnersBinding by viewBinding {
        runnerAdapter.interaction = null
        rvRunners.adapter = null
        rvDistanceType.adapter = null
        circleMenuLayoutManager = null
    }
    private lateinit var runnerAdapter: RunnerListAdapter
    private lateinit var distanceAdapter: DistanceListAdapter
    private var circleMenuLayoutManager: CircleMenuManager? = null


    private var alertDialog: AlertDialog? = null

    private var raceId: String by argument()
    private var raceName: String by argument()
    private var distanceId: String? by argumentNullable()

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withTitle(raceName)
        .withMenu(R.menu.menu_search_with_settings)
        .withMenuItems(
            listOf(R.id.action_settings, R.id.action_result, R.id.action_select_race),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onOpenSettingsFragmentClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onShowResultsClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onSelectRaceClicked()
                true
            })
        )
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) {
            viewModel.onSearchQueryChanged(it)
        }
        .build()

    override fun bindViewModel() {
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                distance.collect {
                    distanceAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                runners.collect{
                    runnerAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                showRunnersTitle.collect{
                    binding.tvRunnersTitle.text = it
                }
            }

            showConfirmationDialog.observe(viewLifecycleOwner) {
                alertDialog?.dismiss()
                when (it) {
                    is AlertTypeConfirmOfftrack -> showConfirmOffTrackDialog(it.position)
                    is AlertTypeMarkRunnerAtCheckpoint -> showConfirmMarkRunnerAtCheckpointDialog(it.position)
                    else -> Unit
                }
            }

            showSuccessDialog.observe(viewLifecycleOwner, {
                val checkpointName = it?.first?.getName() ?: ""
                val message = getString(R.string.success_message, checkpointName, "#${it.second}")
                SuccessDialogFragment.getInstance(message)
                    .show(childFragmentManager, SuccessDialogFragment.name())
            })

            showSelectCheckpointDialog.observe(viewLifecycleOwner, { arrayOfCheckpointViews ->
                val dialog = findFragmentByTag<SelectCheckpointDialogFragment>(SelectCheckpointDialogFragment.name())
                if(dialog == null){
                    SelectCheckpointDialogFragment.getInstance(arrayOfCheckpointViews) {
                        viewModel.onNewCurrentCheckpointSelected(it)
                    }.show(childFragmentManager, SelectCheckpointDialogFragment.name())
                }
            })

            closeSelectCheckpointDialog.observe(viewLifecycleOwner, { selectedCheckpoint ->
                findFragmentByTag<SelectCheckpointDialogFragment>(SelectCheckpointDialogFragment.name())?.dismiss()
                binding.tvCurrentCheckpoint.text = selectedCheckpoint
            })

            showProgress.observe(viewLifecycleOwner, {
                //TODO fix progress
            })

            showTime.observe(viewLifecycleOwner, {
                binding.tvTime.text = getString(R.string.competition_time, it)
            })
        }
    }

    override fun initViews() {
        with(binding) {
            rvRunners.apply {
                runnerAdapter = RunnerListAdapter(this@RunnersFragment)
                addItemDecoration(DividerVerticalItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                adapter = runnerAdapter
            }
            rvDistanceType.apply {
                distanceAdapter = DistanceListAdapter(this@RunnersFragment)
                addItemDecoration(DividerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                adapter = distanceAdapter
            }
            viewModel.initFragment(raceId, distanceId)

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
        val orderSwipeActionHelper = object : SwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedRunner = runnerAdapter.currentList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onRunnerSwipedLeft(position, swipedRunner)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.onRunnerSwipedRight(position, swipedRunner)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(binding.rvRunners)
    }

    override fun onItemSelected(item: RunnerView) {
        viewModel.onClickedAtRunner(item.number, item.actualDistanceId)
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

        fun getInstance(raceId: String, raceName: String,  distanceId: String?) = RunnersFragment().apply {
            this.raceId = raceId
            this.raceName = raceName
            this.distanceId = distanceId
        }
    }
}
