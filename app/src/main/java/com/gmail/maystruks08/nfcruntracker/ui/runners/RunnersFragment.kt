package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.text.InputType
import android.view.MenuItem
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
import com.gmail.maystruks08.nfcruntracker.core.ext.*
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeConfirmOfftrack
import com.gmail.maystruks08.nfcruntracker.ui.runner.AlertTypeMarkRunnerAtCheckpoint
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.DistanceAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.RunnerListAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.SwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SelectCheckpointDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_runners.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
class RunnersFragment : BaseFragment(R.layout.fragment_runners), RunnerListAdapter.Interaction {

    lateinit var viewModel: RunnersViewModel

    private lateinit var runnerAdapter: RunnerListAdapter
    private lateinit var distanceAdapter: DistanceAdapter

    private var alertDialog: AlertDialog? = null

    private var runnerTypeId: Int by argument()

    override fun injectDependencies() {
        App.runnersComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
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
            when(it){
                is AlertTypeConfirmOfftrack -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.alert_confirm_offtrack_runner))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.onRunnerOffTrack()
                            runnerAdapter.notifyItemChanged(it.position)
                            alertDialog?.dismiss()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            runnerAdapter.notifyItemChanged(it.position)
                            alertDialog?.dismiss()
                        }
                    alertDialog = builder.show()
                }
                is AlertTypeMarkRunnerAtCheckpoint -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.mark_runner_without_card))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.markCheckpointAsPassed()
                            runnerAdapter.notifyItemChanged(it.position)
                            alertDialog?.dismiss()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            runnerAdapter.notifyItemChanged(it.position)
                            alertDialog?.dismiss()
                        }
                    alertDialog = builder.show()
                }
                else -> Unit
            }
        })


        viewModel.showDialog.observe(viewLifecycleOwner, {
            val checkpointName = it?.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message)
                .show(childFragmentManager, SuccessDialogFragment.name())
        })

        viewModel.showSelectCheckpointDialog.observe(viewLifecycleOwner, {
            SelectCheckpointDialogFragment.getInstance(0) {
                viewModel.onNewCurrentCheckpointSelected(it)
            }.show(childFragmentManager, SelectCheckpointDialogFragment.name())
        })

        viewModel.showProgress.observe(viewLifecycleOwner, {
           //TODO fix progress
        })

        viewModel.showTime.observe(viewLifecycleOwner, {
            tvTime.text = getString(R.string.competition_time, it)
        })
    }

    @ExperimentalCoroutinesApi
    override fun initViews() {
        runnerAdapter = RunnerListAdapter(this)
        rvRunners.apply {
            layoutManager = LinearLayoutManager(rvRunners.context)
            adapter = runnerAdapter
        }
        distanceAdapter = DistanceAdapter {
            tvRunnersTitle.text = "Название дистанции"
            viewModel.changeRunnerType(it.id)
        }
        rvDistanceType.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = distanceAdapter
        }
        viewModel.initFragment(runnerTypeId)

        btnRegisterNewRunner.setOnClickListener {
            viewModel.onRegisterNewRunnerClicked()
        }
        tvCurrentCheckpoint.setOnClickListener {
            viewModel.onCurrentCheckpointTextClicked()
        }

        setUpItemTouchHelper()
    }

    private fun setUpItemTouchHelper() {
        val orderSwipeActionHelper = object : SwipeActionHelper(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val swipedRunner =  runnerAdapter.currentList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onRunnerSwipedLeft(position, swipedRunner)
                } else if (direction == ItemTouchHelper.RIGHT ) {
                    viewModel.onRunnerSwipedRight(position, swipedRunner)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(rvRunners)
    }

    override fun onItemSelected(item: RunnerView) {
        viewModel.onClickedAtRunner(item.number, item.type)
    }

    fun receiveRunnerUpdateFromServer(runnerChange: RunnerChange) {
        if (isVisible) {
            viewModel.handleRunnerChanges(runnerChange)
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModel.onNfcCardScanned(cardId)
    }

    override fun onStop() {
        super.onStop()
        hideSoftKeyboard(inputManager)
    }

    private fun hideSoftKeyboard(imm: InputMethodManager) {
        toolbarManager?.clearSearch()
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    private val inputManager: InputMethodManager by lazy {
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onDestroyView() {
        runnerAdapter.interaction = null
        rvRunners.adapter = null
        super.onDestroyView()
    }

    override fun clearInjectedComponents() = App.clearRunnersComponent()

    companion object {

        fun getInstance(runnerTypeId: Int) = RunnersFragment().apply {
            this.runnerTypeId = runnerTypeId
        }
    }
}
