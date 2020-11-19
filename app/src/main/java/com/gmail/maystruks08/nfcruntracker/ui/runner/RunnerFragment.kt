package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.ui.runners.SuccessDialogFragment
import kotlinx.android.synthetic.main.fragment_runner.*

class RunnerFragment : BaseFragment(R.layout.fragment_runner) {

    lateinit var viewModel: RunnerViewModel

    private var alertDialog: AlertDialog? = null

    private var runnerNumber: Int by argument()

    private var runnerType: Int by argument()

    private var checkpointsAdapter: CheckpointsAdapter? = null

    override fun injectDependencies() {
        App.runnerComponent?.inject(this)
        viewModel = injectViewModel(viewModeFactory)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withMenu(R.menu.menu_off_track)
        .withMenuItems(
            listOf(R.id.action_runner_off_track, R.id.action_runner_link_card),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onRunnerOffTrackClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onLinkCardToRunnerClicked()
                true
            })
        )
        .withTitle(R.string.screen_runner)
        .build()

    override fun bindViewModel() {
        viewModel.onShowRunnerClicked(runnerNumber)

        viewModel.runner.observe(viewLifecycleOwner, { runner ->
            val numberStr = "#" + runner.number
            tvRunnerNumber.text = numberStr
            tvRunnerFullName.text = runner.fullName
            tvDateOfBirthday.text = runner.dateOfBirthday
            tvRunnerCity.text = runner.city
            tvRunnerCardId.text = runner.cardId
            when {
                runner.isOffTrack -> {
                    val buttonText = getString(R.string.off_track)
                    btnMarkCheckpointAsPassedInManual.text = buttonText
                    btnMarkCheckpointAsPassedInManual.isEnabled = false
                    btnMarkCheckpointAsPassedInManual.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_main_btn_red)
                }
                runner.result != null -> {
                    val totalResultStr = getString(R.string.total_time, runner.result)
                    btnMarkCheckpointAsPassedInManual.text = totalResultStr
                    btnMarkCheckpointAsPassedInManual.isEnabled = false
                    btnMarkCheckpointAsPassedInManual.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_main_btn_green)

                }
                else -> {
                    btnMarkCheckpointAsPassedInManual.text = getString(R.string.mark_at_current_checkpoint)
                    btnMarkCheckpointAsPassedInManual.isEnabled = true
                    btnMarkCheckpointAsPassedInManual.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_main_btn)
                }
            }
            checkpointsAdapter?.isOffTrack = runner.isOffTrack
            checkpointsAdapter?.checkpoints = runner.progress.toMutableList()
        })

        viewModel.linkCardModeEnable.observe(viewLifecycleOwner, {
            if(it){
                runnerCheckpointsRecyclerView.visibility = View.GONE
                tvPleaseScanCard.visibility = View.VISIBLE
                btnMarkCheckpointAsPassedInManual.isEnabled = true
                btnMarkCheckpointAsPassedInManual.text = getString(R.string.disable_link_card_mode)
            } else {
                tvPleaseScanCard.visibility = View.GONE
                runnerCheckpointsRecyclerView.visibility = View.VISIBLE
                btnMarkCheckpointAsPassedInManual.text = getString(R.string.mark_at_current_checkpoint)
            }
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            alertDialog?.dismiss()
            when(it){
                AlertType.CONFIRM_OFFTRACK -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.alert_confirm_offtrack_runner))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.onRunnerOffTrack()
                            alertDialog?.dismiss()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            alertDialog?.dismiss()
                        }
                    alertDialog = builder.show()
                }
                AlertType.MARK_RUNNER_AT_CHECKPOINT -> {
                    val builder = AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.mark_runner_without_card))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.markCheckpointAsPassed(runnerNumber)
                            alertDialog?.dismiss()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            alertDialog?.dismiss()
                        }
                    alertDialog = builder.show()
                }
                else -> Unit
            }
        })

        viewModel.showSuccessDialog.observe(viewLifecycleOwner, {
            val checkpointName = it.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message).show(childFragmentManager, SuccessDialogFragment.name())
        })

    }

    override fun initViews() {
        btnMarkCheckpointAsPassedInManual.setOnClickListener {
            viewModel.btnMarkCheckpointAsPassedInManualClicked()
        }

        checkpointsAdapter = CheckpointsAdapter(::onCheckpointDateLongClicked)
        runnerCheckpointsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnerCheckpointsRecyclerView.context)
            adapter = checkpointsAdapter
        }
    }

    private fun onCheckpointDateLongClicked(checkpointId: Int) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.remove_checkpoint_for_runner))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.deleteCheckpointFromRunner(runnerNumber, checkpointId)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()
    }

    override fun onDestroyView() {
        alertDialog?.dismiss()
        alertDialog = null
        checkpointsAdapter = null
        runnerCheckpointsRecyclerView.adapter = null
        super.onDestroyView()
    }

    override fun clearInjectedComponents() =  App.clearRunnerComponent()

    companion object {

        fun getInstance(runnerNumber: Int, runnerType: Int) = RunnerFragment().apply {
            this.runnerNumber = runnerNumber
            this.runnerType = runnerType
        }
    }
}
