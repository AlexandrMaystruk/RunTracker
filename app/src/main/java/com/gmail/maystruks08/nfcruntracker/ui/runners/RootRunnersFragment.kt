package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.content.Context
import android.text.InputType
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.getVisibleFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.injectViewModel
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_view_pager_runners.*


class RootRunnersFragment : BaseFragment(R.layout.fragment_view_pager_runners) {

    lateinit var viewModel: RootRunnersViewModel

    private var adapter: ScreenSlidePagerAdapter? = null

    private var inputManager: InputMethodManager? = null

    override fun injectDependencies() {
        App.rootRunnersComponent?.inject(this)
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
            val fragment = adapter?.getCurrentVisibleFragment(pager.currentItem)
            fragment?.viewModel?.onSearchQueryChanged(it)
        }
        .build()

    override fun bindViewModel() {
        btnRegisterNewRunner.setOnClickListener {
            viewModel.onRegisterNewRunnerClicked()
        }

        viewModel.invalidateRunnerList.observe(viewLifecycleOwner, {
            adapter?.invalidateRunnerList()
        })
    }

    override fun initViews() {
        inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        adapter = ScreenSlidePagerAdapter(
            ::onClickedAtRunner,
            arrayOf(getString(R.string.runner), getString(R.string.iron)),
            childFragmentManager
        )
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)
    }

    fun receiveRunnerUpdateFromServer(runnerChange: RunnerChange){
        adapter?.getCurrentVisibleFragment(pager.currentItem)?.viewModel?.handleRunnerChanges(
            runnerChange
        )
    }

    fun onNfcCardScanned(cardId: String) {
        getVisibleFragment<RegisterNewRunnerFragment>()?.viewModel?.onNfcCardScanned(cardId)
        getVisibleFragment<RunnerFragment>()?.viewModel?.onNfcCardScanned(cardId)
        adapter?.getCurrentVisibleFragment(pager.currentItem)?.viewModel?.onNfcCardScanned(cardId)
    }

    private fun onClickedAtRunner(runnerView: RunnerView) {
        viewModel.onClickedAtRunner(runnerView.number, runnerView.type)
    }

    override fun onStop() {
        super.onStop()
        hideSoftKeyboard(inputManager)
    }

    private fun hideSoftKeyboard(imm: InputMethodManager?) {
        toolbarManager?.clearSearch()
        imm?.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    override fun onDestroyView() {
        val inputMethodManager: InputMethodManager? = getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        adapter = null
        pager.adapter = null
        App.clearRootRunnersComponent()
        super.onDestroyView()
    }
}
