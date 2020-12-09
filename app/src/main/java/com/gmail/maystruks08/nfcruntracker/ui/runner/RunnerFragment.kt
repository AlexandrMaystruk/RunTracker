package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs.SuccessDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunnerFragment : BaseFragment() {

    private val viewModel: RunnerViewModel by viewModels()

    private lateinit var binding: FragmentRunnerBinding
    private lateinit var checkpointsAdapter: CheckpointsAdapter

    private var alertDialog: AlertDialog? = null
    private var runnerNumber: Int by argument()
    private var runnerType: Int by argument()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRunnerBinding.inflate(inflater, container, false).let {
        binding = it
        it.root
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
            checkpointsAdapter.isOffTrack = runner.isOffTrack
            checkpointsAdapter.checkpoints = runner.progress.toMutableList()
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
            val checkpointName = it.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message).show(childFragmentManager, SuccessDialogFragment.name())
        })

    }

    override fun initViews() {
        with(binding) {
            btnMarkCheckpointAsPassedInManual.setOnClickListener {
                viewModel.btnMarkCheckpointAsPassedInManualClicked()
            }
            runnerCheckpointsRecyclerView.apply {
                layoutManager = LinearLayoutManager(runnerCheckpointsRecyclerView.context)
                checkpointsAdapter = CheckpointsAdapter(::onCheckpointDateLongClicked)
                adapter = checkpointsAdapter
            }
        }
    }

    fun onNfcCardScanned(cardId: String) {
        viewModel.onNfcCardScanned(cardId)
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
        binding.runnerCheckpointsRecyclerView.adapter = null
        super.onDestroyView()
    }


    companion object {

        fun getInstance(runnerNumber: Int, runnerType: Int) = RunnerFragment().apply {
            this.runnerNumber = runnerNumber
            this.runnerType = runnerType
        }
    }
}
