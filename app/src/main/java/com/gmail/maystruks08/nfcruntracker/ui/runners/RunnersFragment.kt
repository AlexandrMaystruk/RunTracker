package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.argumentNullable
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx
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


@ObsoleteCoroutinesApi
@AndroidEntryPoint
class RunnersFragment : BaseFragment(), RunnerListAdapter.Interaction,
    DistanceAdapter.Interaction {

    private val viewModel: RunnersViewModel by viewModels()

    private lateinit var binding: FragmentRunnersBinding
    private lateinit var runnerAdapter: RunnerListAdapter
    private lateinit var distanceAdapter: DistanceAdapter

    private var alertDialog: AlertDialog? = null

    private var raceId: Long by argument()
    private var distanceId: Long? by argumentNullable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRunnersBinding.inflate(inflater, container, false)
        .let { runnersBinding ->
            binding = runnersBinding
            return@let runnersBinding.root
        }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withTitle(R.string.app_name)
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
        viewModel.distance.observe(viewLifecycleOwner, {
            distanceAdapter.items = it
        })

        viewModel.runners.observe(viewLifecycleOwner, {
            runnerAdapter.submitList(it)
        })

        viewModel.showConfirmationDialog.observe(viewLifecycleOwner) {
            alertDialog?.dismiss()
            when (it) {
                is AlertTypeConfirmOfftrack -> showConfirmOffTrackDialog(it.position)
                is AlertTypeMarkRunnerAtCheckpoint -> showConfirmMarkRunnerAtCheckpointDialog(it.position)
                else -> Unit
            }
        }

        viewModel.showSuccessDialog.observe(viewLifecycleOwner, {
            val checkpointName = it?.first?.getName() ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message)
                .show(childFragmentManager, SuccessDialogFragment.name())
        })

        viewModel.showSelectCheckpointDialog.observe(viewLifecycleOwner, { arrayOfCheckpointViews ->
            SelectCheckpointDialogFragment.getInstance(arrayOfCheckpointViews) {
                viewModel.onNewCurrentCheckpointSelected(it)
            }.show(childFragmentManager, SelectCheckpointDialogFragment.name())
        })

        viewModel.showProgress.observe(viewLifecycleOwner, {
            //TODO fix progress
        })

        viewModel.showTime.observe(viewLifecycleOwner, {
            binding.tvTime.text = getString(R.string.competition_time, it)
        })
    }

    @ExperimentalCoroutinesApi
    override fun initViews() {
        with(binding) {
            rvRunners.apply {
                runnerAdapter = RunnerListAdapter(this@RunnersFragment)
                addItemDecoration(DividerVerticalItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                layoutManager = LinearLayoutManager(binding.rvRunners.context)
                adapter = runnerAdapter
            }
            rvDistanceType.apply {
                distanceAdapter = DistanceAdapter(this@RunnersFragment)
                addItemDecoration(DividerItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_s)))
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = distanceAdapter
            }
            viewModel.initFragment(raceId, distanceId)
            btnRegisterNewRunner.setOnClickListener { viewModel.onRegisterNewRunnerClicked() }
            tvCurrentCheckpoint.setOnClickListener { viewModel.onCurrentCheckpointTextClicked() }
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
        binding.tvRunnersTitle.text = distance.name
        viewModel.changeDistance(distance.id)
    }

    fun receiveRunnerUpdateFromServer(runnerChange: RunnerChange) {
        if (isVisible) {
            viewModel.handleRunnerChanges(runnerChange)
        }
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

    override fun onDestroyView() {
        runnerAdapter.interaction = null
        binding.rvRunners.adapter = null
        super.onDestroyView()
    }

    companion object {

        fun getInstance(raceId: Long, distanceId: Long?) = RunnersFragment().apply {
            this.raceId = raceId
            this.distanceId = distanceId
        }
    }
}
