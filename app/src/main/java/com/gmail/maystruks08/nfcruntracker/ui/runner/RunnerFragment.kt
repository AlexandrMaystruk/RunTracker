package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.view_binding_extentions.viewBinding
import com.gmail.maystruks08.nfcruntracker.databinding.FragmentRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.DividerVerticalItemDecoration
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers.DetailViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerDetailView
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamDetailView
import com.gmail.maystruks08.nfcruntracker.ui.main.dialogs.SuccessDialogFragment
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RunnerFragment : BaseFragment(R.layout.fragment_runner),
    RunnerCheckpointsViewHolderManager.Interaction {

    private val viewModel: RunnerViewModel by viewModels()
    private val binding: FragmentRunnerBinding by viewBinding {
        rvRunnersDetail.adapter = null
    }
    private lateinit var detailItemAdapter: AppAdapter

    private var alertDialog: AlertDialog? = null
    private var runnerNumber: String by argument()

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runner)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailItemAdapter = AppAdapter(listOf(DetailViewHolderManager(this)))
    }

    override fun bindViewModel() {
        with(viewModel) {
            onShowRunnerClicked(runnerNumber)
            runner.observe(viewLifecycleOwner, { runner ->
                when (runner) {
                    is RunnerDetailView -> {
                        binding.llTeamHeader.visibility = View.GONE
                        detailItemAdapter.submitList(listOf(runner))
                    }
                    is TeamDetailView -> {
                        binding.llTeamHeader.visibility = View.VISIBLE
                        binding.tvTeamName.text = runner.id
                        binding.tvTeamResult.text = runner.teamResult
                        detailItemAdapter.submitList(runner.runners)
                    }
                }
            })
            showSuccessDialog.observe(viewLifecycleOwner, {
                val checkpointName = it.first?.getName() ?: ""
                val message = getString(R.string.success_message, checkpointName, "#${it.second}")
                SuccessDialogFragment.getInstance(message)
                    .show(childFragmentManager, SuccessDialogFragment.name())
            })
        }
    }

    override fun initViews() {
        with(binding) {
            rvRunnersDetail.adapter = detailItemAdapter
            rvRunnersDetail.addItemDecoration(
                DividerVerticalItemDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.margin_s
                    )
                )
            )
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

        fun getInstance(runnerNumber: String) = RunnerFragment().apply {
            this.runnerNumber = runnerNumber
        }
    }
}
