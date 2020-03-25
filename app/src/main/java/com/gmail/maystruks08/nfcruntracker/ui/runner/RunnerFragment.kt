package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateFormat
import kotlinx.android.synthetic.main.fragment_runner.*
import kotlinx.android.synthetic.main.fragment_runner.tvRunnerFullName

import javax.inject.Inject

class RunnerFragment : BaseFragment(R.layout.fragment_runner) {

    @Inject
    lateinit var viewModel: RunnerViewModel

    private var runnerId: String by argument()

    private var checkpointsAdapter: CheckpointsAdapter? = null

    override fun injectDependencies() {
        App.runnerComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.app_name)
        .build()

    override fun bindViewModel() {
        viewModel.onShowRunnerClicked(runnerId)

        viewModel.runner.observe(viewLifecycleOwner, Observer { runner ->
            val numberStr = "#" + runner.number
            tvRunnerNumber.text = numberStr
            tvRunnerFullName.text = runner.fullName
            tvDateOfBirthday.text = runner.dateOfBirthday.toDateFormat()
            checkpointsAdapter?.checkpoints = runner.checkpoints.toMutableList()
        })
    }

    override fun initViews() {
        btnMarkCheckpointAsPassedInManual.setOnClickListener {
            viewModel.markCheckpointAsPassed(runnerId)
        }

        checkpointsAdapter = CheckpointsAdapter()
        runnerCheckpointsRecyclerView.apply {
            layoutManager = LinearLayoutManager(runnerCheckpointsRecyclerView.context)
            adapter = checkpointsAdapter
        }
    }

    override fun onDestroyView() {
        App.clearRunnerComponent()
        super.onDestroyView()
    }

    companion object {

        fun getInstance(runnerId: String) = RunnerFragment().apply { this.runnerId = runnerId }
    }
}
