package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnersBinding
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.DistanceAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.RunnerListAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.SwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SelectCheckpointDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
class RunnersFragment : BaseFragment(), RunnerListAdapter.Interaction,
    DistanceAdapter.Interaction {

    private lateinit var binding: FragmentRunnersBinding
    private lateinit var viewModel: RunnersViewModel

    private lateinit var runnerAdapter: RunnerListAdapter
    private lateinit var distanceAdapter: DistanceAdapter

    private var alertDialog: AlertDialog? = null

    private var runnerTypeId: Int by argument()

    override fun injectDependencies() {
        App.runnersComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

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
            listOf(R.id.action_settings, R.id.action_result),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onOpenSettingsFragmentClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onShowResultsClicked()
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

        viewModel.showConfirmationDialog.observe(viewLifecycleOwner, {
            alertDialog?.dismiss()
            when (it) {
                is AlertTypeConfirmOfftrack -> showConfirmOffTrackDialog(it.position)
                is AlertTypeMarkRunnerAtCheckpoint -> showConfirmMarkRunnerAtCheckpointDialog(it.position)
                else -> Unit
            }
        })

        viewModel.showSuccessDialog.observe(viewLifecycleOwner, {
            val checkpointName = it?.first?.name ?: ""
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
        runnerAdapter = RunnerListAdapter(this)
        binding.rvRunners.apply {
            layoutManager = LinearLayoutManager(binding.rvRunners.context)
            adapter = runnerAdapter
        }
        distanceAdapter = DistanceAdapter(this)
        binding.rvDistanceType.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = distanceAdapter
        }
        viewModel.initFragment(runnerTypeId)

        binding.btnRegisterNewRunner.setOnClickListener {
            viewModel.onRegisterNewRunnerClicked()
        }
        binding.tvCurrentCheckpoint.setOnClickListener {
            viewModel.onCurrentCheckpointTextClicked()
        }

        setUpItemTouchHelper()
    }

    private fun setUpItemTouchHelper() {
        val orderSwipeActionHelper = object : SwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedRunner =  runnerAdapter.currentList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onRunnerSwipedLeft(position, swipedRunner)
                } else if (direction == ItemTouchHelper.RIGHT ) {
                    viewModel.onRunnerSwipedRight(position, swipedRunner)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(binding.rvRunners)
    }

    override fun onItemSelected(item: RunnerView) {
        viewModel.onClickedAtRunner(item.number, item.type)
    }

    override fun onItemSelected(item: DistanceView) {
        binding.tvRunnersTitle.text = item.name
        viewModel.changeRunnerType(item.id)
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

    override fun clearInjectedComponents() = App.clearRunnersComponent()

    companion object {

        fun getInstance(runnerTypeId: Int) = RunnersFragment().apply {
            this.runnerTypeId = runnerTypeId
        }
    }
}
