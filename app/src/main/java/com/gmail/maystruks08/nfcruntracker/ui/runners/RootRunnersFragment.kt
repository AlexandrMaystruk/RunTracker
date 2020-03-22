package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.text.InputType
import android.view.MenuItem
import com.gmail.maystruks08.nfcruntracker.App
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.base.BaseFragment
import com.gmail.maystruks08.nfcruntracker.core.base.FragmentToolbar
import com.gmail.maystruks08.nfcruntracker.core.ext.getChildVisibleFragment
import com.gmail.maystruks08.nfcruntracker.core.ext.getVisibleFragment
import com.gmail.maystruks08.nfcruntracker.ui.register.RegisterNewRunnerFragment
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.fragment_view_pager_runners.*
import javax.inject.Inject

class RootRunnersFragment : BaseFragment(R.layout.fragment_view_pager_runners) {

    @Inject
    lateinit var viewModel: RootRunnersViewModel

    private var adapter: ScreenSlidePagerAdapter? = null

    override fun injectDependencies() = App.rootRunnersComponent?.inject(this)

    override fun initToolbar() = FragmentToolbar.Builder()
        .withId(R.id.toolbar)
        .withTitle(R.string.app_name)
        .withMenu(R.menu.menu_search_with_settings)
        .withMenuItems(
            listOf(R.id.action_settings, R.id.action_logout),
            listOf(MenuItem.OnMenuItemClickListener {
                viewModel.onOpenSettingsFragmentClicked()
                true
            }, MenuItem.OnMenuItemClickListener {
                viewModel.onSignOutClicked()
                true
            })
        )
        .withMenuSearch(InputType.TYPE_CLASS_NUMBER) {
            val fragment = adapter?.getRegisteredFragment(pager.currentItem)
            fragment?.viewModel?.onSearchQueryChanged(it)
        }
        .build()

    override fun bindViewModel() {
        btnRegisterNewRunner.setOnClickListener {
            viewModel.onRegisterNewRunnerClicked()
        }
    }

    override fun initViews() {
        adapter = ScreenSlidePagerAdapter(::onClickedAtRunner, childFragmentManager)
        pager.adapter = adapter
        tabs.setupWithViewPager(pager)
    }

    fun onNfcCardScanned(cardId: String) {
        getVisibleFragment<RegisterNewRunnerFragment>()?.viewModel?.onNfcCardScanned(cardId)
        getChildVisibleFragment<RunnersFragment>()?.viewModel?.onNfcCardScanned(cardId)
    }

    private fun onClickedAtRunner(runnerView: RunnerView) {
        viewModel.onClickedAtRunner(runnerView)
    }

    override fun onDestroyView() {
        adapter = null
        pager.adapter = null
        App.clearRootRunnersComponent()
        super.onDestroyView()
    }
}