package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import kotlinx.android.synthetic.main.fragment_runner.*
import javax.inject.Inject

class RunnerFragment : BaseFragment(R.layout.fragment_runner) {

    @Inject
    lateinit var viewModel: RunnerViewModel

    private var alertDialog: AlertDialog? = null

    private var runnerId: String by argument()

    private var checkpointsAdapter: CheckpointsAdapter? = null

    override fun injectDependencies() {
        App.runnerComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.screen_runner)
        .build()

    override fun bindViewModel() {
        viewModel.onShowRunnerClicked(runnerId)

        viewModel.runnerWithCheckpoints.observe(viewLifecycleOwner, Observer { runner ->
            val numberStr = "#" + runner.first.number
            tvRunnerNumber.text = numberStr
            tvRunnerFullName.text = runner.first.fullName
            tvDateOfBirthday.text = runner.first.dateOfBirthday
            tvRunnerCity.text = runner.first.city
            if(runner.first.result != null){
                val totalResultStr = "Общее время: ${runner.first.result}"
                btnMarkCheckpointAsPassedInManual.text = totalResultStr
                btnMarkCheckpointAsPassedInManual.isEnabled = false
            } else {
                btnMarkCheckpointAsPassedInManual.text = "Отметить на текущем КП"
                btnMarkCheckpointAsPassedInManual.isEnabled = true
            }
            checkpointsAdapter?.checkpoints = runner.second.toMutableList()
        })
    }

    override fun initViews() {
        btnMarkCheckpointAsPassedInManual.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
                .setTitle("Внимание!")
                .setMessage("Отметить участника на КП без карты?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.markCheckpointAsPassed(runnerId)
                    alertDialog?.dismiss()
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    alertDialog?.dismiss()
                }
            alertDialog = builder.show()
        }

        checkpointsAdapter = CheckpointsAdapter(::onCheckpointDateLongClicked)
        runnerCheckpointsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnerCheckpointsRecyclerView.context)
            adapter = checkpointsAdapter
        }
    }

    private fun onCheckpointDateLongClicked(checkpointId: Int){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Внимание!")
            .setMessage("Удалить участнику прохождение текущего КП?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                viewModel.deleteCheckpointFromRunner(runnerId, checkpointId)
                alertDialog?.dismiss()
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                alertDialog?.dismiss()
            }
        alertDialog = builder.show()
    }

    override fun onDestroyView() {
        App.clearRunnerComponent()
        super.onDestroyView()
    }

    companion object {

        fun getInstance(runnerId: String) = RunnerFragment().apply { this.runnerId = runnerId }
    }
}
