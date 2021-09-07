package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunnerFragment : BaseFragment(R.layout.fragment_runner),
    RunnerCheckpointsViewHolderManager.Interaction {

    private val viewModel: RunnerViewModel by viewModels()
    private val binding: FragmentRunnerBinding by viewBinding {
        runnerCheckpointsRecyclerView.adapter = null
    }
    private lateinit var checkpointsAdapter: AppAdapter

    private var alertDialog: AlertDialog? = null
    private var runnerNumber: String by argument()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkpointsAdapter = AppAdapter(listOf(RunnerCheckpointsViewHolderManager(this)))
    }

    override fun bindViewModel() {
        viewModel.onShowRunnerClicked(runnerNumber)

        viewModel.runner.observe(viewLifecycleOwner, { runner ->
            with(binding) {
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
            }
            checkpointsAdapter.submitList(runner.progress)
        })

        viewModel.linkCardModeEnable.observe(viewLifecycleOwner, {
            with(binding) {
                if (it) {
                    runnerCheckpointsRecyclerView.visibility = View.GONE
                    tvPleaseScanCard.visibility = View.VISIBLE
                    btnMarkCheckpointAsPassedInManual.isEnabled = true
                    btnMarkCheckpointAsPassedInManual.text =
                        getString(R.string.disable_link_card_mode)
                } else {
                    tvPleaseScanCard.visibility = View.GONE
                    runnerCheckpointsRecyclerView.visibility = View.VISIBLE
                    btnMarkCheckpointAsPassedInManual.text =
                        getString(R.string.mark_at_current_checkpoint)
                }
            }
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            alertDialog?.dismiss()
            when(it){
                is AlertTypeConfirmOfftrack -> {
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
                is AlertTypeMarkRunnerAtCheckpoint -> {
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
            val checkpointName = it.first?.getName() ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message).show(childFragmentManager, SuccessDialogFragment.name())
        })

    }

    override fun initViews() {
        with(binding) {
            btnMarkCheckpointAsPassedInManual.setOnClickListener {
                viewModel.btnMarkCheckpointAsPassedInManualClicked()
            }
            runnerCheckpointsRecyclerView.adapter = checkpointsAdapter
        }
    }

    override fun onLongCLickAtCheckpointDate(item: CheckpointView) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.remove_checkpoint_for_runner))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.deleteCheckpointFromRunner(runnerNumber, item)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()
    }

    fun onNfcCardScanned(cardId: String) {
        viewModel.onNfcCardScanned(cardId)
    }


    override fun onDestroyView() {
        alertDialog?.dismiss()
        alertDialog = null
        super.onDestroyView()
    }

    companion object {

        fun getInstance(runnerNumber: String, distanceId: String) = RunnerFragment().apply {
            this.runnerNumber = runnerNumber
        }
    }
}
