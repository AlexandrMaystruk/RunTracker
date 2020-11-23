package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.text.InputType
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.argument
import com.gmail.maystruks08.nfcruntracker.core.ext.name
import com.gmail.maystruks08.nfcruntracker.core.ext.setVisibility
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.DistanceAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.RunnerListAdapter
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_runners.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

class RunnersFragment : BaseFragment(R.layout.fragment_runners), RunnerListAdapter.Interaction {

    /**
     * Created RunnersViewModel without ViewModel providers
     * because I don't know how I can create different instances.
     * If used ViewModel providers for a different instance of the fragment
     * will injected the same instance of RunnersViewModel
     */
    @Inject
    lateinit var viewModel: RunnersViewModel

    private lateinit var runnerAdapter: RunnerListAdapter
    private lateinit var distanceAdapter: DistanceAdapter

    private var runnerTypeId: Int by argument()

    override fun injectDependencies() {
        Timber.e("TIME injectDependencies ${System.currentTimeMillis()}")
        App.runnersComponent?.inject(this)
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

        viewModel.showDialog.observe(viewLifecycleOwner, {
            val checkpointName = it.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message)
                .show(childFragmentManager, SuccessDialogFragment.name())
        })

        viewModel.showProgress.observe(viewLifecycleOwner, {
            runnerProgressBar.setVisibility(it)
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
