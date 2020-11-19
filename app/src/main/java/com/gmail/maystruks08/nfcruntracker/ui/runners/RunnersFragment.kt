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
import kotlinx.android.synthetic.main.fragment_runners.*
import kotlinx.android.synthetic.main.layout_runners_content.*
import kotlinx.android.synthetic.main.layout_runners_header.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

class RunnersFragment : BaseFragment(R.layout.fragment_runners) {

    /**
     * Created RunnersViewModel without ViewModel providers
     * because I don't know how I can create different instances.
     * If used ViewModel providers for a different instance of the fragment
     * will injected the same instance of RunnersViewModel
     */
    @Inject
    lateinit var viewModel: RunnersViewModel

    private lateinit var runnerAdapter: RunnerAdapter
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
            runnerAdapter.runnerList = it
        })

        viewModel.runnerAdd.observe(viewLifecycleOwner, {
            runnerAdapter.insertItemOrUpdateIfExist(it)
        })

        viewModel.runnerUpdate.observe(viewLifecycleOwner, {
            runnerAdapter.updateItem(it)
        })

        viewModel.runnerRemove.observe(viewLifecycleOwner, {
            runnerAdapter.removeItem(it)
        })

        viewModel.showDialog.observe(viewLifecycleOwner, {
            val checkpointName = it.first?.name ?: ""
            val message = getString(R.string.success_message, checkpointName, "#${it.second}")
            SuccessDialogFragment.getInstance(message)
                .show(childFragmentManager, SuccessDialogFragment.name())
        })
    }

    @ExperimentalCoroutinesApi
    override fun initViews() {
        runnerAdapter = RunnerAdapter { viewModel.onClickedAtRunner(it.number, it.type) }
        rvRunners.apply {
            layoutManager = LinearLayoutManager(rvRunners.context)
            adapter = runnerAdapter
        }
        distanceAdapter = DistanceAdapter {
            tvDistanceHeader.text = it.name
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
