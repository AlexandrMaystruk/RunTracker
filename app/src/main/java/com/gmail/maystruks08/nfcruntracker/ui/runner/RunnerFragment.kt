package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.Observer
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import kotlinx.android.synthetic.main.fragment_runner.*

import javax.inject.Inject

class RunnerFragment : BaseFragment(R.layout.fragment_runner) {

    @Inject
    lateinit var viewModel: RunnerViewModel

    private var runner: RunnerView by argument()

    override fun injectDependencies() {
        App.runnerComponent?.inject(this)
    }

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withNavigationIcon(R.drawable.ic_arrow_back) { viewModel.onBackClicked() }
        .withTitle(R.string.app_name)
        .build()

    override fun bindViewModel() {
        viewModel.runner.observe(viewLifecycleOwner, Observer { runnerView ->
            tvRunnerName.text = runnerView.name
            tvRunnerSurname.text = runnerView.surname
            tvDateOfBirthday.text = runnerView.dateOfBirthday
            checkpointStepProgress.setStepViewTexts(runnerView.checkpoints.map { it.stepBean })
        })
        viewModel.onShowRunnerClicked(runner)
    }

    override fun initViews() {
        btnMarkCheckpointAsPassedInManual.setOnClickListener {
            viewModel.markCheckpointAsPassed(runner)
        }
    }

    override fun onDestroyView() {
        App.clearRunnerComponent()
        super.onDestroyView()
    }

    companion object {

        fun getInstance(runner: RunnerView) = RunnerFragment().apply { this.runner = runner }
    }
}
